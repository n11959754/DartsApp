package com.darts.dartsapp.util;

import com.darts.dartsapp.model.Tasks;
import com.darts.dartsapp.model.TasksTable;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AITextToResponse {

    static final String LLAMA3 = "llama3.1";
    static final String host = "http://localhost:11434/";

    public static void generateAndStoreTasks(String selectedAssignment, String allocatedTimeText, String dueDay, String dueTime, int assignmentID, int hours) {
        String prompt = String.format( // This is the prompt the AI is given
                "I have an assignment called \"%s\" due on %s at %s. " +
                        "Today is %s. I want to work on it for %s hours total. " +
                        "Please break this assignment into smaller tasks, each as a bullet point starting with '•'. " +
                        "Each task should include a short title, an estimated duration in full hours (which means no decimals/half hours) in parentheses, and a short description. " +
                        "Make sure all durations add up to %s hours. Please format the list of tasks with the dot point as previously stated, followed by **title of task** (x hours). then on a line underneath the description." +
                        "Please keep your response under 45 words.",
                selectedAssignment, dueDay, dueTime, LocalDate.now(), allocatedTimeText, allocatedTimeText
        );

        try {
            OllamaAPI ollamaAPI = new OllamaAPI(host);
            ollamaAPI.setRequestTimeoutSeconds(120); // timer for how long before prompt times out

            OllamaResult result = ollamaAPI.generate(
                    LLAMA3,
                    prompt,
                    false,
                    new OptionsBuilder().build()
            );

            String response = result.getResponse();
            System.out.println("\nFull response captured.");



            String[] lines = response.split("\\r?\\n");
            TasksTable tasksTable = new TasksTable();

            String currentTitle = null;
            int currentDuration = 1;
            StringBuilder currentDescription = new StringBuilder();

            for (String line : lines) {
                line = line.trim();

                if (line.startsWith("•")) {
                    // Save previous task
                    if (currentTitle != null) {
                        String description = currentDescription.toString().trim();
                        if (description.isEmpty()) description = "(No description)";
                        String fullTask = currentTitle + " - " + description;
                        tasksTable.createTask(new Tasks(assignmentID, fullTask, currentDuration));
                    }

                    currentDescription.setLength(0);
                    currentDuration = 1;

                    Matcher matcher = Pattern.compile("•\\s*(?:\\*\\*)?(.*?)\\*?\\*?\\s*\\((\\d+(?:\\.\\d+)?)\\s*(?:hours?)?\\)").matcher(line);
                    if (matcher.find()) {
                        currentTitle = matcher.group(1).trim();
                        currentDuration = Integer.parseInt(matcher.group(2).trim());
                        System.out.println("✅ Matched: " + currentTitle + " (" + currentDuration + " hours)"); // show the format is working on AI for testing
                    } else {
                        currentTitle = line.substring(1).trim(); // If format on AI isn't work for testing
                        System.out.println("⚠️ Failed to match pattern in: " + line);
                    }

                } else if (!line.isEmpty() && currentTitle != null) {
                    currentDescription.append(line).append(" ");
                }
            }


            if (currentTitle != null) {
                String description = currentDescription.toString().trim();
                if (description.isEmpty()) description = "(No description)";
                String fullTask = currentTitle + " - " + description;
                tasksTable.createTask(new Tasks(assignmentID, fullTask, currentDuration));
            }


            System.out.println("✅ Tasks successfully stored.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
