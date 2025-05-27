package com.darts.dartsapp.controller.calendar;

import com.darts.dartsapp.model.ClassTable;
import com.darts.dartsapp.model.ClassTimeSlot;
import com.darts.dartsapp.model.ClassTimeSlotTable;
import com.darts.dartsapp.model.Session; // Assuming Session and User model for fetching user-specific classes
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CalendarController {

    // FXML Fields
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;

    // styles for the calendar cells
    private static final String DATE_CELL_STYLE = "-fx-background-color: #dae3eb; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String EMPTY_CELL_STYLE = "-fx-background-color: #ECEFF1; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String SELECTED_CELL_STYLE = "-fx-background-color: #ECEFF1; -fx-border-color: #0288D1; -fx-border-width: 1px;";

    // controller state
    private YearMonth currentYearMonth;
    private Node currentlySelectedCell = null;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy"); // Corrected pattern

    // class data structure
    private static class Event {
        String time;
        String name;
        String style;
        int timeSlotId; // Corresponds to 'id' column in TimeSlots table

        public Event(String time, String name, String style, int timeSlotId) {
            this.time = time;
            this.name = name;
            this.style = style;
            this.timeSlotId = timeSlotId;
        }

        public int getTimeSlotId() {
            return timeSlotId;
        }
    }
    private final Map<DayOfWeek, List<Event>> weeklyEvents = new HashMap<>();
    private ClassTimeSlotTable timeSlotTable; // Instance of ClassTimeSlotTable

    // starts the controller class
    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        timeSlotTable = new ClassTimeSlotTable(); // Initialize here
        loadWeeklyEvents();
        drawCalendar();
    }

    // database interaction for classes
    private List<ClassTimeSlot> fetchClassTimeSlotsFromDB() {
        List<ClassTimeSlot> allSlots = new ArrayList<>();

        // Assuming Session management for user context
        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            int userID = Session.getCurrentUser().getUserID(); // Assuming User model has getUserID()
            ClassTable classTable = new ClassTable(); // Consider injecting or making a service

            // Get all classes associated with the user
            List<com.darts.dartsapp.model.Class> userClasses = classTable.getClassesByUser(userID);

            for (com.darts.dartsapp.model.Class userClass : userClasses) {
                // For each class, get its scheduled time slots
                List<ClassTimeSlot> slotsForClass = timeSlotTable.getTimeSlotsByClassID(userClass.getClassID());
                allSlots.addAll(slotsForClass);
            }
        } else {
            // If no user is logged in, or for a general calendar view, you might fetch all slots
            // For now, returning empty if not logged in to maintain user-specific view
            // return timeSlotTable.getAllClassTimeSlots(); // Uncomment for all slots if needed
            System.out.println("User not logged in or no current user, no classes loaded.");
        }
        return allSlots;
    }

    // helper methods
    private DayOfWeek mapStringToDayOfWeek(String dayString) {
        if (dayString == null) return null;
        try {
            return DayOfWeek.valueOf(dayString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid day string for DayOfWeek mapping: " + dayString);
            return null;
        }
    }

    // text color
    private String determineTextColor(String backgroundColorHex) {
        if (backgroundColorHex == null || !backgroundColorHex.startsWith("#") ||
                (backgroundColorHex.length() != 7 && backgroundColorHex.length() != 9)) {
            return "black";
        }
        try {
            Color color = Color.web(backgroundColorHex);
            double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
            return luminance > 0.5 ? "black" : "white";
        } catch (Exception e) {
            System.err.println("Error determining text color for background: " + backgroundColorHex + " - " + e.getMessage());
            return "black";
        }
    }

    // classes logic
    private void loadWeeklyEvents() {
        weeklyEvents.clear();
        List<ClassTimeSlot> classSlots = fetchClassTimeSlotsFromDB();

        for (ClassTimeSlot slot : classSlots) {
            DayOfWeek dayOfWeek = mapStringToDayOfWeek(slot.getDay());
            if (dayOfWeek == null) {
                System.err.println("Warning: Could not parse day: '" + slot.getDay() + "' for classID: " + slot.getClassID() + ", timeSlotID: " + slot.getTimeSlotID());
                continue;
            }
            String eventName = slot.getType(); // Using type as the event name
            String slotColour = slot.getColour() != null ? slot.getColour() : "#CCCCCC"; // Default color
            String textColor = determineTextColor(slotColour);
            String eventStyle = "-fx-background-color: " + slotColour + "; -fx-text-fill: " + textColor + ";";

            weeklyEvents.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                    .add(new Event(slot.getTime(), eventName, eventStyle, slot.getTimeSlotID()));
        }

        for (List<Event> eventsOnDay : weeklyEvents.values()) {
            eventsOnDay.sort(Comparator.comparing(e -> e.time));
        }
    }

    // calendar and navigation
    @FXML
    private void handlePreviousMonth(@SuppressWarnings("unused") ActionEvent event) {
        currentYearMonth = currentYearMonth.minusMonths(1);
        clearSelection();
        drawCalendar();
    }

    @FXML
    private void handleNextMonth(@SuppressWarnings("unused") ActionEvent event) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        clearSelection();
        // loadWeeklyEvents(); // uncomment if the classes change dynamically per month idk
        drawCalendar();
    }

    private void drawCalendar() {
        monthYearLabel.setText(currentYearMonth.format(monthYearFormatter).toUpperCase());
        populateCalendarGrid();
    }

    // creates the calendar group
    private void populateCalendarGrid() {
        if (calendarGrid == null) {
            System.err.println("Error: calendarGrid is null cannot draw calendar.");
            return;
        }
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7; // sun=0, mon=1... sat=6
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int currentDay = 1;

        for (int row = 1; row <= 6; row++) { // starts from row 1 for the dates (row 0 is for the day headers)
            for (int col = 0; col < 7; col++) {
                if (calendarGrid.getRowCount() <= row) break; // hopefully grid has enough rows

                StackPane cell;
                if ((row == 1 && col < dayOfWeekOffset) || currentDay > daysInMonth) {
                    cell = createEmptyCell();
                } else {
                    LocalDate date = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(), currentDay);
                    cell = createDateCell(date);
                    currentDay++;
                }
                calendarGrid.add(cell, col, row);
            }
        }
    }

    // calendar month date cells
    private StackPane createDateCell(LocalDate date) {
        StackPane cell = new StackPane();
        cell.setOnMouseClicked(this::handleDateCellClick);
        cell.setStyle(DATE_CELL_STYLE);
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        StackPane.setAlignment(dateLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(dateLabel, new Insets(2, 5, 0, 0));
        VBox contentBox = new VBox(2);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(2));
        contentBox.getChildren().add(dateLabel);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<Event> eventsForDay = weeklyEvents.get(dayOfWeek);

        if (eventsForDay != null) {
            for (Event event : eventsForDay) {
                Label eventLabel = new Label(event.time + " " + event.name);
                eventLabel.setFont(Font.font("System", FontWeight.NORMAL, 9));
                eventLabel.setPadding(new Insets(1, 3, 1, 3));
                eventLabel.setStyle(event.style + " -fx-background-radius: 3;");
                eventLabel.setMaxWidth(Double.MAX_VALUE); // labels fills width
                VBox.setMargin(eventLabel, new Insets(1, 0, 0, 0));
                eventLabel.setUserData(event);
                eventLabel.setOnMouseClicked(this::handleClassEventClick);
                contentBox.getChildren().add(eventLabel);
            }
        }
        cell.getChildren().add(contentBox);
        return cell;
    }

    // empty cells also makes them unclickable
    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.setPadding(new Insets(5));
        cell.setMouseTransparent(true);
        cell.setStyle(EMPTY_CELL_STYLE);
        return cell;
    }

    @FXML
    private void handleDateCellClick(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        StackPane clickedCell = null;

        if (sourceNode instanceof StackPane) {
            clickedCell = (StackPane) sourceNode;
        } else if (sourceNode.getParent() instanceof StackPane) {
            clickedCell = (StackPane) sourceNode.getParent();
        }

        // won't process clicks if it's on an empty cell
        if (clickedCell == null || clickedCell.getStyle().equals(EMPTY_CELL_STYLE)) {
            event.consume();
            return;
        }

        if (currentlySelectedCell != null && currentlySelectedCell != clickedCell) {
            if (!currentlySelectedCell.getStyle().equals(EMPTY_CELL_STYLE)) {
                currentlySelectedCell.setStyle(DATE_CELL_STYLE);
            }
        }

        clickedCell.setStyle(SELECTED_CELL_STYLE);
        currentlySelectedCell = clickedCell;
    }

    // allows user to delete a class on click
    private void handleClassEventClick(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        Event eventData = (Event) clickedLabel.getUserData();

        if (eventData != null) {
            int timeSlotIdToDelete = eventData.getTimeSlotId();
            String eventDetails = eventData.time + " " + eventData.name;
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Delete Class Time Slot?");
            confirmationDialog.setContentText("Are you sure you want to delete the class: " + eventDetails + "?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // uses the deleteClassTimeSlot method from ClassTimeSlotTable
                    timeSlotTable.deleteClassTimeSlot(timeSlotIdToDelete);

                    // shows success dialogue to user
                    System.out.println("Successfully initiated deletion for time slot ID: " + timeSlotIdToDelete);

                    // redraws the calendar with updated events
                    loadWeeklyEvents();
                    drawCalendar();
                    clearSelection();
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Deletion Successful");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("The class '" + eventDetails + "' has been deleted.");
                    infoAlert.showAndWait();

                } catch (Exception e) {
                    // catches any potential exceptions from the delete operation or ui surface updates
                    System.err.println("Error during deletion or UI refresh for time slot ID: " + timeSlotIdToDelete);
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Could not delete the class. An unexpected error occurred: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        }
        event.consume(); // consumeeee
    }

    private void clearSelection() {
        if (currentlySelectedCell != null) {
            if (!currentlySelectedCell.getStyle().equals(EMPTY_CELL_STYLE)) {
                currentlySelectedCell.setStyle(DATE_CELL_STYLE);
            }
            currentlySelectedCell = null;
        }
    }
}