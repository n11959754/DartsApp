package com.darts.dartsapp.controller.calendar;

import com.darts.dartsapp.model.ClassTable;
import com.darts.dartsapp.model.ClassTimeSlot; // Correct import
import com.darts.dartsapp.model.ClassTimeSlotTable;
import com.darts.dartsapp.model.Session;

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
import javafx.scene.control.ButtonBar; // Added for ButtonData

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

public class CalendarController {

    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;

    // colours for date cells
    private static final String DATE_CELL_STYLE = "-fx-background-color: #dae3eb; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String EMPTY_CELL_STYLE = "-fx-background-color: #ECEFF1; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String SELECTED_CELL_STYLE = "-fx-background-color: #c5d9e8; -fx-border-color: #0288D1; -fx-border-width: 1px;";

    private YearMonth currentYearMonth;
    private Node currentlySelectedCell = null;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMMuuuu");

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

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        timeSlotTable = new ClassTimeSlotTable();
        classTable = new ClassTable();
        setupHeader();
        loadWeeklyEvents();
        drawCalendar();
    }

    private void setupHeader() {
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPadding(new Insets(5));
            dayLabel.setStyle("-fx-background-color: #CFD8DC; -fx-border-color: #B0BEC5; -fx-border-width: 0 0 1px 0;");
            if (calendarGrid.getColumnConstraints().size() > i || calendarGrid.getColumnCount() > i) {
                calendarGrid.add(dayLabel, i, 0);
            } else {
                System.err.println("CalendarGrid does not have enough columns for header. Column: " + i);
            }
        }
    }

    private List<ClassTimeSlot> fetchClassTimeSlotsFromDB() {
        List<ClassTimeSlot> allSlots = new ArrayList<>();
        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            int userID = Session.getCurrentUser().getUserID();
            List<com.darts.dartsapp.model.Class> userParentClasses = classTable.getClassesByUser(userID);
            for (com.darts.dartsapp.model.Class userClass : userParentClasses) {
                List<ClassTimeSlot> slotsForClass = timeSlotTable.getTimeSlotsByClassID(userClass.getClassID());
                allSlots.addAll(slotsForClass);
            }
        } else {
            return new ArrayList<>();
        }
        return allSlots;
    }

    private DayOfWeek mapStringToDayOfWeek(String dayString) {
        if (dayString == null) return null;
        try {
            return DayOfWeek.valueOf(dayString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid day string from database: " + dayString);
            return null;
        }
    }

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
            return "black";
        }
    }

    private void loadWeeklyEvents() {
        weeklyEvents.clear();
        Map<Integer, String> classIdToNameMap = new HashMap<>();

        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            List<com.darts.dartsapp.model.Class> userParentClasses = classTable.getClassesByUser(Session.getCurrentUser().getUserID());
            for (com.darts.dartsapp.model.Class parentClass : userParentClasses) {
                classIdToNameMap.put(parentClass.getClassID(), parentClass.getClassName());
            }
        } else {
            return;
        }

        List<ClassTimeSlot> classSlots = fetchClassTimeSlotsFromDB();
        for (ClassTimeSlot slot : classSlots) {
            DayOfWeek dayOfWeek = mapStringToDayOfWeek(slot.getDay());
            if (dayOfWeek == null) continue;

            String slotType = slot.getType() != null ? slot.getType() : "Event";
            String parentClassName = classIdToNameMap.getOrDefault(slot.getClassID(), "Unknown Class");
            String slotColour = (slot.getColour() != null && slot.getColour().matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$"))
                    ? slot.getColour() : "#CCCCCC";
            String textColor = determineTextColor(slotColour);
            String eventStyle = "-fx-background-color: " + slotColour + "; -fx-text-fill: " + textColor + ";";

            weeklyEvents.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                    .add(new Event(slot.getTime(), slotType, parentClassName, eventStyle, slot.getTimeSlotID()));
        }

        for (List<Event> eventsOnDay : weeklyEvents.values()) {
            eventsOnDay.sort(Comparator.comparing((Event e) -> e.time)
                    .thenComparing(e -> e.parentClassName)
                    .thenComparing(e -> e.type));
        }
    }

    @FXML
    private void handlePreviousMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.minusMonths(1);
        clearSelection();
        drawCalendar();
    }

    @FXML
    private void handleNextMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        clearSelection();
        drawCalendar();
    }

    private void drawCalendar() {
        monthYearLabel.setText(currentYearMonth.format(monthYearFormatter).toUpperCase());
        populateCalendarGrid();
    }

    private void populateCalendarGrid() {
        if (calendarGrid == null) return;
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int currentDay = 1;

        for (int row = 1; row <= 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (row >= calendarGrid.getRowCount() && calendarGrid.getRowCount() > 0) {
                    System.err.println("Attempting to add cell to non-existent row: " + row + ", Max rows: " + calendarGrid.getRowCount());
                    break;
                }
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
            if (row >= calendarGrid.getRowCount() && calendarGrid.getRowCount() > 0) break;
        }
    }

    private StackPane createDateCell(LocalDate date) {
        StackPane cell = new StackPane();
        cell.setUserData(date);
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
        cell.getChildren().add(contentBox);
        return cell;
    }

    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.setPadding(new Insets(5));
        cell.setMouseTransparent(true);
        cell.setStyle(EMPTY_CELL_STYLE);
        return cell;
    }

    @FXML
    private void handleDateCellClick(MouseEvent event) {
        StackPane clickedCell = getStackPane(event);

        if (clickedCell == null || clickedCell.getStyle().equals(EMPTY_CELL_STYLE) || clickedCell.getUserData() == null || !(clickedCell.getUserData() instanceof LocalDate selectedDate)) {
            event.consume();
            return;
        }

        if (currentlySelectedCell != null && currentlySelectedCell != clickedCell) {
            if (!currentlySelectedCell.getStyle().equals(EMPTY_CELL_STYLE) && currentlySelectedCell.getUserData() instanceof LocalDate) {
                currentlySelectedCell.setStyle(DATE_CELL_STYLE);
            }
        }
        clickedCell.setStyle(SELECTED_CELL_STYLE);
        currentlySelectedCell = clickedCell;
        showAddClassDialog(selectedDate);
        event.consume();
    }

    private static StackPane getStackPane(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        StackPane clickedCell = null;

        if (sourceNode instanceof StackPane) {
            clickedCell = (StackPane) sourceNode;
        } else {
            Node parent = sourceNode.getParent();
            if (parent instanceof StackPane) {
                clickedCell = (StackPane) parent;
            } else if (parent != null && parent.getParent() instanceof StackPane) {
                clickedCell = (StackPane) parent.getParent();
            }
        }
        return clickedCell;
    }

    private void showAddClassDialog(LocalDate date) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Class Slot");
        dialog.setHeaderText("Add new weekly slot for: " + date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "s");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField classCodeField = new TextField();
        final List<com.darts.dartsapp.model.Class> userClasses;

        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            userClasses = classTable.getClassesByUser(Session.getCurrentUser().getUserID());
        } else {
            new Alert(Alert.AlertType.WARNING, "You must be logged in to add or create class slots.").showAndWait();
            resetCellSelectionAfterDialog();
            return;
        }

        if (userClasses.isEmpty()) {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("No Existing Classes");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("You don't have any classes registered yet. You can create a new one by entering its name.");
            infoAlert.showAndWait();
        }

        classCodeField.setPromptText("Enter Class Name");
        TextField typeField = new TextField();
        typeField.setPromptText("e.g., Lecture, Tutorial");
        TextField timeField = new TextField();
        timeField.setPromptText("HH:mm (e.g., 10:00)");
        ColorPicker colorPicker = new ColorPicker(Color.LIGHTBLUE);

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

        GridPane.setColumnSpan(classCodeField, GridPane.REMAINING);
        GridPane.setColumnSpan(typeField, GridPane.REMAINING);
        GridPane.setColumnSpan(timeField, GridPane.REMAINING);
        GridPane.setColumnSpan(colorPicker, GridPane.REMAINING);
        dialogPane.setContent(grid);

        Node okButtonNode = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButtonNode.setDisable(true);

        Runnable validateInputs = () -> {
            boolean timeValid = timeField.getText().matches("^([01]\\d|2[0-3]):([0-5]\\d)$");
            boolean classNameEntered = !classCodeField.getText().trim().isEmpty();
            okButtonNode.setDisable(
                    !classNameEntered ||
                            typeField.getText().trim().isEmpty() ||
                            timeField.getText().trim().isEmpty() || !timeValid
            );
        };

        classCodeField.textProperty().addListener((obs, ov, nv) -> validateInputs.run());
        typeField.textProperty().addListener((obs, ov, nv) -> validateInputs.run());
        timeField.textProperty().addListener((obs, ov, nv) -> validateInputs.run());
        validateInputs.run();

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String enteredClassName = classCodeField.getText().trim();
            com.darts.dartsapp.model.Class selectedClass = null;

            Optional<com.darts.dartsapp.model.Class> foundClassOpt = userClasses.stream()
                    .filter(c -> c.getClassName() != null && c.getClassName().equalsIgnoreCase(enteredClassName)) // Ensure getClassName is not null
                    .findFirst();

            if (foundClassOpt.isPresent()) {
                selectedClass = foundClassOpt.get();
            } else {
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
                            Session.getCurrentUser().getUserID(),
                            enteredClassName
                    );
                    selectedClass = classTable.createClass(newClassToCreate);

                    if (selectedClass == null || selectedClass.getClassID() == 0) {
                        new Alert(Alert.AlertType.ERROR, "Failed to create the new class '" + enteredClassName + "'. Slot not added.").showAndWait();
                        resetCellSelectionAfterDialog();
                        return;
                    }
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Action cancelled. Class slot not added.").showAndWait();
                    resetCellSelectionAfterDialog();
                    return;
                }
            }

            if (selectedClass == null) {
                resetCellSelectionAfterDialog();
                return;
            }

            String type = typeField.getText().trim();
            String timeInput = timeField.getText().trim();
            String dayOfWeekStr = date.getDayOfWeek().toString();
            Color selectedColor = colorPicker.getValue();
            String colorHex = String.format("#%02x%02x%02x",
                    (int) (selectedColor.getRed() * 255),
                    (int) (selectedColor.getGreen() * 255),
                    (int) (selectedColor.getBlue() * 255));

            ClassTimeSlot newSlot = new ClassTimeSlot(
                    selectedClass.getClassID(),
                    timeInput,
                    dayOfWeekStr,
                    type
            );
            newSlot.setColour(colorHex);

            try {
                timeSlotTable.createClassTimeSlot(newSlot);
                new Alert(Alert.AlertType.INFORMATION, "Class slot added successfully for '" + selectedClass.getClassName() + "'!").showAndWait();
                loadWeeklyEvents();
                drawCalendar();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to add class slot. Error: " + e.getMessage()).showAndWait();
            }
        }
        resetCellSelectionAfterDialog();
    }

    private void resetCellSelectionAfterDialog() {
        if (currentlySelectedCell != null) {
            if (!currentlySelectedCell.getStyle().equals(EMPTY_CELL_STYLE) && currentlySelectedCell.getUserData() instanceof LocalDate) {
                currentlySelectedCell.setStyle(DATE_CELL_STYLE);
            }
            currentlySelectedCell = null;
        }
    }

    private void handleClassEventClick(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        Event eventData = (Event) clickedLabel.getUserData();

        if (eventData != null) {
            int timeSlotIdToDelete = eventData.getTimeSlotId();
            String eventDetails = eventData.time + " " + eventData.parentClassName + " - " + eventData.type;
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Delete Class Time Slot?");
            confirmationDialog.setContentText("Are you sure you want to delete: " + eventDetails + "?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    timeSlotTable.deleteClassTimeSlot(timeSlotIdToDelete);
                    loadWeeklyEvents();
                    drawCalendar();
                    clearSelection();
                    new Alert(Alert.AlertType.INFORMATION, "The class '" + eventDetails + "' has been deleted.").showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Could not delete the class. Error: " + e.getMessage()).showAndWait();
                }
            }
        }
        event.consume();
    }

    private void clearSelection() {
        resetCellSelectionAfterDialog();
    }
}