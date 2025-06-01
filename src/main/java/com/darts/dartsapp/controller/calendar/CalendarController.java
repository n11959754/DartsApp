package com.darts.dartsapp.controller.calendar;

import com.darts.dartsapp.model.ClassTable;
import com.darts.dartsapp.model.ClassTimeSlot;
import com.darts.dartsapp.model.ClassTimeSlotTable;
import com.darts.dartsapp.model.Session;
import com.darts.dartsapp.model.Assignments;
import com.darts.dartsapp.model.AssignmentsTable;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ButtonBar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for the calendar view.
 * Manages the display of months, days, weekly recurring class events, and assignments on the calendar.
 * Allows users to navigate between months, add new weekly class slots, and delete existing ones.
 * Interacts with database tables for classes, class time slots, and assignments.
 */
public class CalendarController {

    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;

    // colours for date cells
    private static final String DATE_CELL_STYLE = "-fx-background-color: #dae3eb; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String EMPTY_CELL_STYLE = "-fx-background-color: #ECEFF1; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String SELECTED_CELL_STYLE = "-fx-background-color: #c5d9e8; -fx-border-color: #0288D1; -fx-border-width: 1px;";

    private YearMonth currentYearMonth;
    private Node currentlySelectedCell = null;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM uuuu");

    //  calendar event with its time, type, class name, display style, ID
    private static class Event {
        String time;
        String type;
        String parentClassName;
        String style;
        int timeSlotId;

        public Event(String time, String type, String parentClassName, String style, int timeSlotId) {
            this.time = time;
            this.type = type;
            this.parentClassName = parentClassName;
            this.style = style;
            this.timeSlotId = timeSlotId;
        }
        public int getTimeSlotId() { return timeSlotId; }
    }

    private final Map<DayOfWeek, List<Event>> weeklyEvents = new HashMap<>();
    private ClassTimeSlotTable timeSlotTable;
    private ClassTable classTable;
    private AssignmentsTable assignmentsTable;

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        timeSlotTable = new ClassTimeSlotTable();
        classTable = new ClassTable(); // Instantiated here
        assignmentsTable = new AssignmentsTable(); // Instantiated here
        setupHeader();
        loadWeeklyEvents();
        drawCalendar();
    }

    // sets up the calendar header row by creating labels for each day of the week sun-sat
    private void setupHeader() {
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPadding(new Insets(5));
            dayLabel.setStyle("-fx-background-color: #CFD8DC; -fx-border-color: #B0BEC5; -fx-border-width: 0 0 1px 0;");
            calendarGrid.add(dayLabel, i, 0);
        }
    }

    // fetches all class time slots from the database for the currently logged-in user
    private List<ClassTimeSlot> fetchClassTimeSlotsFromDB() {
        List<ClassTimeSlot> allSlots = new ArrayList<>();
        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            int userID = Session.getCurrentUser().getUserID();
            // Uses classTable.getClassesByUser
            List<com.darts.dartsapp.model.Class> userParentClasses = classTable.getClassesByUser(userID);
            for (com.darts.dartsapp.model.Class userClass : userParentClasses) {
                List<ClassTimeSlot> slotsForClass = timeSlotTable.getTimeSlotsByClassID(userClass.getClassID()); // Assumes Class has getClassID()
                allSlots.addAll(slotsForClass);
            }
        }
        return allSlots;
    }

    // maps a string representation of a day to the DayOfWeek
    private DayOfWeek mapStringToDayOfWeek(String dayString) {
        if (dayString == null) return null;
        try {
            return DayOfWeek.valueOf(dayString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid day string from database: " + dayString);
            return null;
        }
    }

    // determines whether black or white text is more readable against the background colour
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
            System.err.println("Error determining text color for background: " + backgroundColorHex + ". Defaulting to black. " + e.getMessage());
            return "black";
        }
    }

    // loads classes for the week from the database and makes them ready to be displayed
    private void loadWeeklyEvents() {
        weeklyEvents.clear();
        Map<Integer, String> classIdToNameMap = new HashMap<>();

        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            // Uses classTable.getClassesByUser
            List<com.darts.dartsapp.model.Class> userParentClasses = classTable.getClassesByUser(Session.getCurrentUser().getUserID());
            for (com.darts.dartsapp.model.Class parentClass : userParentClasses) {
                classIdToNameMap.put(parentClass.getClassID(), parentClass.getClassName()); // Assumes Class has getClassID() and getClassName()
            }
        } else {
            return;
        }

        // fetches all class time slots from the database for the logged in user
        List<ClassTimeSlot> classSlots = fetchClassTimeSlotsFromDB();
        for (ClassTimeSlot slot : classSlots) {
            DayOfWeek dayOfWeek = mapStringToDayOfWeek(slot.getDay());
            if (dayOfWeek == null) continue;

            String slotType = slot.getType() != null ? slot.getType() : "Event";
            String parentClassName = classIdToNameMap.getOrDefault(slot.getClassID(), "Unknown Class"); // Assumes ClassTimeSlot has getClassID()
            String slotColour = (slot.getColour() != null && slot.getColour().matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$"))
                    ? slot.getColour() : "#CCCCCC";
            String textColor = determineTextColor(slotColour);
            String eventStyle = "-fx-background-color: " + slotColour + "; -fx-text-fill: " + textColor + ";";

            weeklyEvents.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                    .add(new Event(slot.getTime(), slotType, parentClassName, eventStyle, slot.getTimeSlotID()));
        }

        // sorts the events for each day of the week
        for (List<Event> eventsOnDay : weeklyEvents.values()) {
            eventsOnDay.sort(Comparator.comparing((Event e) -> e.time)
                    .thenComparing(e -> e.parentClassName)
                    .thenComparing(e -> e.type));
        }
    }

    // handles going back a month
    @FXML
    private void handlePreviousMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.minusMonths(1);
        clearSelection();
        drawCalendar();
    }

    // hangles going forwards a month
    @FXML
    private void handleNextMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        clearSelection();
        drawCalendar();
    }

    // redraws the calendar
    private void drawCalendar() {
        monthYearLabel.setText(currentYearMonth.format(monthYearFormatter).toUpperCase());
        populateCalendarGrid();
    }

    // populates the main calendar grid with day cells for the current month
    private void populateCalendarGrid() {
        if (calendarGrid == null) return;
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int currentDay = 1;

        // iterates through the rows of the calendar grid to cover all possible month layouts
        for (int row = 1; row <= 6; row++) {
            for (int col = 0; col < 7; col++) {
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
            if (currentDay > daysInMonth && row >= ( (daysInMonth + dayOfWeekOffset -1) / 7) +1 ) break;
        }
    }

    /**
     * Creates and returns a StackPane representing a single date cell in the calendar.
     * <p>
     * This method constructs a visual cell for a specific date. The cell displays:
     * <ul>
     * <li>The day of the month.</li>
     * <li>Recurring weekly events scheduled for that particular day of the week.</li>
     * <li>Assignments due on this specific date, visible if a user is logged in and
     * has assignments associated with their enrolled classes.</li>
     * </ul>
     * The date cell itself and the event labels within it are interactive,
     * responding to mouse clicks to perform further actions (e.g. viewing details).
     * Assignments are also displayed with their due times and associated class names
     * styled based on their properties.
     * </p>
     *
     * @param date The {@link java.time.LocalDate} for which to create the date cell.
     * This date determines the day number displayed and is used to fetch
     * relevant weekly events and specific assignments that are on those days.
     * @return A {@link javafx.scene.layout.StackPane} representing the visual date cell,
     * populated with the date number, scheduled weekly events and any
     * assignments due on this date. The cell and its contents are styled.
     */
    // creates and returns a StackPane representing a single date cell in the calendar
    private StackPane createDateCell(LocalDate date) {
        StackPane cell = new StackPane();
        cell.setUserData(date);
        cell.setOnMouseClicked(this::handleDateCellClick);
        cell.setStyle(DATE_CELL_STYLE);

        // label for displaying the day number
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        StackPane.setAlignment(dateLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(dateLabel, new Insets(2, 5, 0, 0));

        // VBox to hold the date label and any event labels for this day
        VBox contentBox = new VBox(2);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(2));
        contentBox.getChildren().add(dateLabel);

        // retrieve events scheduled for this particular day of the week
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<Event> eventsForDay = weeklyEvents.get(dayOfWeek);

        // if there are events for this day create and add labels for them
        if (eventsForDay != null) {
            for (Event event : eventsForDay) {
                String displayText = event.time + " " + event.parentClassName + " - " + event.type;
                Label eventLabel = new Label(displayText);
                eventLabel.setFont(Font.font("System", FontWeight.NORMAL, 9));
                eventLabel.setPadding(new Insets(1, 3, 1, 3));
                eventLabel.setStyle(event.style + " -fx-background-radius: 3;");
                eventLabel.setMaxWidth(Double.MAX_VALUE);
                VBox.setMargin(eventLabel, new Insets(1, 0, 0, 0));
                eventLabel.setUserData(event);
                eventLabel.setOnMouseClicked(this::handleClassEventClick);
                contentBox.getChildren().add(eventLabel);
            }
        }

        // adds assignments for this date
        if (Session.isLoggedIn() && Session.getCurrentUser() != null && assignmentsTable != null) {
            int userID = Session.getCurrentUser().getUserID();
            List<com.darts.dartsapp.model.Class> userClasses = classTable.getClassesByUser(userID);
            List<Assignments> assignmentsOnThisDate = new ArrayList<>();

            // returns assignments for current user
            for (com.darts.dartsapp.model.Class userClass : userClasses) {
                List<Assignments> assignmentsForClass = assignmentsTable.getAssignmentsByClassID(userClass.getClassID());
                for (Assignments assignment : assignmentsForClass) {
                    try {
                        LocalDate assignmentDueDate = LocalDate.parse(assignment.getDay());
                        if (assignmentDueDate.equals(date)) {
                            assignmentsOnThisDate.add(assignment);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing assignment due date: " + assignment.getDay() + " for assignment ID: " + (assignment.getClassID()) /* Placeholder for actual assignment ID if available */);
                    }
                }
            }

            // sorts assignments for this date by time
            assignmentsOnThisDate.sort(Comparator.comparing(Assignments::getTime)); // Assumes Assignments.getTime()

            for (Assignments assignment : assignmentsOnThisDate) {
                String classNameString = classTable.getClassNameByID(assignment.getClassID());
                String assignmentText = "[DUE] " + assignment.getTime() + " " + classNameString + " - " + assignment.getType(); // Assumes Assignments.getType()
                Label assignmentLabel = new Label(assignmentText);
                assignmentLabel.setFont(Font.font("System", FontWeight.NORMAL, 9));
                assignmentLabel.setPadding(new Insets(1, 3, 1, 3));

                String assignmentColour = (assignment.getColour() != null && assignment.getColour().matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$"))
                        ? assignment.getColour() : "#E6A756";
                String assignmentTextColor = determineTextColor(assignmentColour);

                assignmentLabel.setStyle(
                        "-fx-background-color: " + assignmentColour + ";" +
                                "-fx-text-fill: " + assignmentTextColor + ";" +
                                "-fx-background-radius: 3;"
                );
                assignmentLabel.setMaxWidth(Double.MAX_VALUE);
                VBox.setMargin(assignmentLabel, new Insets(1, 0, 0, 0));
                contentBox.getChildren().add(assignmentLabel);
            }
        }

        cell.getChildren().add(contentBox);
        return cell;
    }

    // handles empty calendar days
    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.setPadding(new Insets(5));
        cell.setMouseTransparent(true);
        cell.setStyle(EMPTY_CELL_STYLE);
        return cell;
    }

    // handles mouse click events on date cells in the calendar
    @FXML
    private void handleDateCellClick(MouseEvent event) {
        StackPane clickedCell = getStackPaneFromEvent(event);

        if (clickedCell == null || clickedCell.getStyle().equals(EMPTY_CELL_STYLE) || clickedCell.getUserData() == null || !(clickedCell.getUserData() instanceof LocalDate selectedDate)) {
            event.consume();
            return;
        }

        if (currentlySelectedCell != null && currentlySelectedCell != clickedCell) {
            if (currentlySelectedCell.getUserData() instanceof LocalDate) {
                currentlySelectedCell.setStyle(DATE_CELL_STYLE);
            }
        }
        clickedCell.setStyle(SELECTED_CELL_STYLE);
        currentlySelectedCell = clickedCell;
        showAddClassDialog(selectedDate);
        event.consume();
    }

    // handles mouse click events on individual date cells in the calendar grid
    private StackPane getStackPaneFromEvent(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        StackPane targetCell = null;
        Node currentNode = sourceNode;
        while (currentNode != null && !(currentNode instanceof GridPane)) {
            if (currentNode instanceof StackPane && currentNode.getUserData() instanceof LocalDate) {
                targetCell = (StackPane) currentNode;
                break;
            }
            currentNode = currentNode.getParent();
        }
        if (targetCell == null && sourceNode instanceof StackPane && sourceNode.getUserData() instanceof LocalDate) {
            targetCell = (StackPane) sourceNode;
        }
        return targetCell;
    }

    /**
     * Displays a dialog to allow the user to add a new weekly class time slot for a specified date.
     * <p>
     * The dialog prompts the user for the class name, type (e.g. Lecture, Tutorial),
     * time, and a colour for the class. It validates user input, ensuring that a class name
     * and type are entered, and the time is in HH:mm format.
     * </p>
     * <p>
     * Before allowing the addition of a slot, it checks if the user is logged in.
     * It retrieves the user's existing classes. If the entered class name don't match
     * an existing class, it offers the user the option to create a new class with that name.
     * </p>
     * <p>
     * Upon successful validation and confirmation (and potential class creation),
     * a new {@code ClassTimeSlot} is created and persisted. The UI (weekly events and calendar)
     * is then refreshed. Appropriate alerts are shown for success, errors, or cancellations.
     * </p>
     * <p>
     * The 'OK' button in the dialog is disabled until all required fields are valid.
     * After the dialog is closed (either by 'OK', 'Cancel', or an error), the cell
     * selection is reset.
     * </p>
     *
     * @param date The {@link LocalDate} for which the new class time slot is being added.
     * The day of the week from this date is used as a non-editable field in the dialog.
     */
    // displays a dialog menu to allow the user to add a new weekly class time slot for a specified date
    private void showAddClassDialog(LocalDate date) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Class Slot");
        dialog.setHeaderText("Add new weekly slot for: " + date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "s");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // create a GridPane for laying out input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        // user input field for class code
        TextField classCodeField = new TextField();
        final List<com.darts.dartsapp.model.Class> userClasses;

        // checks the user session and retrieve user's classes
        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            userClasses = classTable.getClassesByUser(Session.getCurrentUser().getUserID());
        } else {
            new Alert(Alert.AlertType.WARNING, "You must be logged in to add or create class slots.").showAndWait();
            resetCellSelectionAfterDialog();
            return;
        }

        // tells the user if they have no existing classes registered
        if (userClasses.isEmpty()) {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("No Existing Classes");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("You don't have any classes registered yet. You can create a new one by entering its name.");
            infoAlert.showAndWait();
        }

        // Initialise input fields with prompts
        classCodeField.setPromptText("Enter Class Name");
        TextField typeField = new TextField();
        typeField.setPromptText("e.g., Lecture, Tutorial");
        TextField timeField = new TextField();
        timeField.setPromptText("HH:mm (e.g., 10:00)");
        ColorPicker colorPicker = new ColorPicker(Color.LIGHTBLUE);

        // labels and input fields for the grid
        grid.add(new Label("Selected Day:"), 0, 0);
        grid.add(new Label(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)), 1, 0);
        grid.add(new Label("Class Name:"), 0, 1);
        grid.add(classCodeField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeField, 1, 2);
        grid.add(new Label("Time:"), 0, 3);
        grid.add(timeField, 1, 3);
        grid.add(new Label("Colour:"), 0, 4);
        grid.add(colorPicker, 1, 4);

        // makes the input fields span remaining columns in the grid
        GridPane.setColumnSpan(classCodeField, GridPane.REMAINING);
        GridPane.setColumnSpan(typeField, GridPane.REMAINING);
        GridPane.setColumnSpan(timeField, GridPane.REMAINING);
        GridPane.setColumnSpan(colorPicker, GridPane.REMAINING);
        dialogPane.setContent(grid);

        Node okButtonNode = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButtonNode.setDisable(true);

        // validates user inputs
        Runnable validateInputs = () -> {
            boolean timeValid = timeField.getText().matches("^([01]\\d|2[0-3]):([0-5]\\d)$");
            boolean classNameEntered = !classCodeField.getText().trim().isEmpty();
            okButtonNode.setDisable(!classNameEntered || typeField.getText().trim().isEmpty() || !timeValid);
        };

        classCodeField.textProperty().addListener((obs, ov, nv) -> validateInputs.run());
        typeField.textProperty().addListener((obs, ov, nv) -> validateInputs.run());
        timeField.textProperty().addListener((obs, ov, nv) -> validateInputs.run());
        validateInputs.run();

        Optional<ButtonType> result = dialog.showAndWait();

        // processes the result if the ok button is clicked
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String enteredClassName = classCodeField.getText().trim();
            com.darts.dartsapp.model.Class selectedClass = null;

            Optional<com.darts.dartsapp.model.Class> foundClassOpt = userClasses.stream()
                    .filter(c -> c.getClassName() != null && c.getClassName().equalsIgnoreCase(enteredClassName)) // Assumes Class.getClassName()
                    .findFirst();

            if (foundClassOpt.isPresent()) {
                selectedClass = foundClassOpt.get();
            } else {
                // if no class not found ask the user if they want to create it
                Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationDialog.setTitle("Create New Class?");
                confirmationDialog.setHeaderText("Class Not Found: '" + enteredClassName + "'");
                confirmationDialog.setContentText("This class is not in your registered classes. Do you want to create it and then add this time slot?");

                ButtonType createAndAddButton = new ButtonType("Create and Add Slot");
                ButtonType justCancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                confirmationDialog.getButtonTypes().setAll(createAndAddButton, justCancelButton);
                Optional<ButtonType> confResult = confirmationDialog.showAndWait();

                if (confResult.isPresent() && confResult.get() == createAndAddButton) {
                    com.darts.dartsapp.model.Class newClassToCreate = new com.darts.dartsapp.model.Class(
                            Session.getCurrentUser().getUserID(), enteredClassName
                    );
                    selectedClass = classTable.createClass(newClassToCreate);
                    if (selectedClass == null || selectedClass.getClassID() == 0) {
                        new Alert(Alert.AlertType.ERROR, "Failed to create the new class '" + enteredClassName + "'. Slot not added.").showAndWait();
                        resetCellSelectionAfterDialog();
                        return;
                    }
                } else {
                    // if user cancels class creation inform and exit menu
                    new Alert(Alert.AlertType.INFORMATION, "Action cancelled. Class slot not added.").showAndWait();
                    resetCellSelectionAfterDialog();
                    return;
                }
            }

            if (selectedClass == null) { // Should not happen if logic above is correct, but as a safeguard
                new Alert(Alert.AlertType.ERROR, "Could not identify or create the class. Slot not added.").showAndWait();
                resetCellSelectionAfterDialog();
                return;
            }

            // retrieves slot details from input fields
            String type = typeField.getText().trim();
            String timeInput = timeField.getText().trim();
            String dayOfWeekStr = date.getDayOfWeek().toString();
            Color selectedColorValue = colorPicker.getValue();
            String colorHex = String.format("#%02x%02x%02x",
                    (int) (selectedColorValue.getRed() * 255),
                    (int) (selectedColorValue.getGreen() * 255),
                    (int) (selectedColorValue.getBlue() * 255));

            // creates a new ClassTimeSlot object.
            ClassTimeSlot newSlot = new ClassTimeSlot(
                    selectedClass.getClassID(), // Assumes Class.getClassID()
                    timeInput, dayOfWeekStr, type
            );
            newSlot.setColour(colorHex);

            // attempts to save the new time slot to the database.
            try {
                timeSlotTable.createClassTimeSlot(newSlot);
                new Alert(Alert.AlertType.INFORMATION, "Class slot added successfully for '" + selectedClass.getClassName() + "'!").showAndWait(); // Assumes Class.getClassName()
                loadWeeklyEvents();
                drawCalendar();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to add class slot. Error: " + e.getMessage()).showAndWait();
            }
        }
        resetCellSelectionAfterDialog();
    }

    //  resets the visual selection state of any currently selected calendar day
    private void resetCellSelectionAfterDialog() {
        if (currentlySelectedCell != null) {
            if (currentlySelectedCell.getUserData() instanceof LocalDate) {
                currentlySelectedCell.setStyle(DATE_CELL_STYLE);
            }
            currentlySelectedCell = null;
        }
    }

    // handles the mouse click events on individual class event labels within a date cell
    private void handleClassEventClick(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        Event eventData = (Event) clickedLabel.getUserData();

        // proceeds only if there is valid event data associated with the clicked label
        if (eventData != null) {
            int timeSlotIdToDelete = eventData.getTimeSlotId();
            String eventDetails = eventData.time + " " + eventData.parentClassName + " - " + eventData.type;
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Delete Class Time Slot?");
            confirmationDialog.setContentText("Are you sure you want to delete: " + eventDetails + "?");

            // show the dialog and wait for the user's response
            Optional<ButtonType> result = confirmationDialog.showAndWait();

            // if the user clicks ok to confirm deletion proceed to try statement to delete event from db
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    timeSlotTable.deleteClassTimeSlot(timeSlotIdToDelete);
                    loadWeeklyEvents();
                    drawCalendar();
                    clearSelection();
                    new Alert(Alert.AlertType.INFORMATION, "The class time slot '" + eventDetails + "' has been deleted.").showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Could not delete the class time slot. Error: " + e.getMessage()).showAndWait();
                }
            }
        }
        event.consume();
    }
    private void clearSelection() {
        resetCellSelectionAfterDialog();
    }
}