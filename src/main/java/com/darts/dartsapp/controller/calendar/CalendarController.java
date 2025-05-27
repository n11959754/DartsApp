package com.darts.dartsapp.controller.calendar;
import com.darts.dartsapp.model.ClassTable;
import com.darts.dartsapp.model.ClassTimeSlot;
import com.darts.dartsapp.model.ClassTimeSlotTable;
import com.darts.dartsapp.model.Session;
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
    private static final String SELECTED_CELL_STYLE = "-fx-background-color: #c5d9e8; -fx-border-color: #0288D1; -fx-border-width: 1px;"; // Adjusted selection color

    // controller state
    private YearMonth currentYearMonth;
    private Node currentlySelectedCell = null;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    // **MODIFIED Event class to include parentClassName and specific type**
    private static class Event {
        String time;
        String type; // e.g., Lecture, Tutorial
        String parentClassName; // e.g., CAB204
        String style;
        int timeSlotId;

        public Event(String time, String type, String parentClassName, String style, int timeSlotId) {
            this.time = time;
            this.type = type;
            this.parentClassName = parentClassName;
            this.style = style;
            this.timeSlotId = timeSlotId;
        }

        public int getTimeSlotId() {
            return timeSlotId;
        }
    }
    private final Map<DayOfWeek, List<Event>> weeklyEvents = new HashMap<>();
    private ClassTimeSlotTable timeSlotTable;
    private ClassTable classTable;

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        timeSlotTable = new ClassTimeSlotTable();
        classTable = new ClassTable();
        loadWeeklyEvents();
        drawCalendar();
    }

    // fetches parent class names
    private List<ClassTimeSlot> fetchClassTimeSlotsFromDB() {
        List<ClassTimeSlot> allSlots = new ArrayList<>();
        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            int userID = Session.getCurrentUser().getUserID();
            // gets all parent classes for the user to map their names later
            List<com.darts.dartsapp.model.Class> userParentClasses = classTable.getClassesByUser(userID);

            for (com.darts.dartsapp.model.Class userClass : userParentClasses) {
                List<ClassTimeSlot> slotsForClass = timeSlotTable.getTimeSlotsByClassID(userClass.getClassID());
                allSlots.addAll(slotsForClass);
            }
        } else {
            return timeSlotTable.getAllClassTimeSlots();
        }
        return allSlots;
    }


    private DayOfWeek mapStringToDayOfWeek(String dayString) {
        if (dayString == null) return null;
        try {
            return DayOfWeek.valueOf(dayString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
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

    // classes logic
    private void loadWeeklyEvents() {
        weeklyEvents.clear();

        // map for easy lookup
        Map<Integer, String> classIdToNameMap = new HashMap<>();
        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            List<com.darts.dartsapp.model.Class> userParentClasses = classTable.getClassesByUser(Session.getCurrentUser().getUserID());
            for (com.darts.dartsapp.model.Class parentClass : userParentClasses) {
                classIdToNameMap.put(parentClass.getClassID(), parentClass.getClassName());
            }
        }

        List<ClassTimeSlot> classSlots = fetchClassTimeSlotsFromDB();
        for (ClassTimeSlot slot : classSlots) {
            DayOfWeek dayOfWeek = mapStringToDayOfWeek(slot.getDay());
            if (dayOfWeek == null) {
                continue;
            }
            String slotType = slot.getType(); // e.g. "Lecture", "Tutorial"
            String parentClassName = classIdToNameMap.getOrDefault(slot.getClassID(), "Unknown Class");
            String slotColour = slot.getColour() != null ? slot.getColour() : "#CCCCCC";
            String textColor = determineTextColor(slotColour);
            String eventStyle = "-fx-background-color: " + slotColour + "; -fx-text-fill: " + textColor + ";";

            weeklyEvents.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                    .add(new Event(slot.getTime(), slotType, parentClassName, eventStyle, slot.getTimeSlotID()));
        }

        // sorts by time then by parent class name then by type
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
        loadWeeklyEvents();
        drawCalendar();
    }

    @FXML
    private void handleNextMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        clearSelection();
        loadWeeklyEvents();
        drawCalendar();
    }

    private void drawCalendar() {
        monthYearLabel.setText(currentYearMonth.format(monthYearFormatter).toUpperCase());
        populateCalendarGrid();
    }

    private void populateCalendarGrid() {
        if (calendarGrid == null) {
            return;
        }
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int currentDay = 1;

        for (int row = 1; row <= 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (calendarGrid.getRowCount() <= row) break;
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

    // displays parent class name and type (class type and class code)
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

    // creates the empty cells on the calendar
    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.setPadding(new Insets(5));
        cell.setMouseTransparent(true);
        cell.setStyle(EMPTY_CELL_STYLE);
        return cell;
    }

    // on click functions for the cells
    @FXML
    private void handleDateCellClick(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        StackPane clickedCell = null;

        if (sourceNode instanceof StackPane) {
            clickedCell = (StackPane) sourceNode;
        } else if (sourceNode.getParent() instanceof StackPane) {
            clickedCell = (StackPane) sourceNode.getParent();
        }

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

    // displays classes on days in the calendar
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
                    loadWeeklyEvents(); // reload events to reflect deletion
                    drawCalendar();
                    clearSelection();
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Deletion Successful");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("The class '" + eventDetails + "' has been deleted.");
                    infoAlert.showAndWait();
                } catch (Exception e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Could not delete the class. An unexpected error occurred: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        }
        event.consume();
    }

    private void clearSelection() {
        if (currentlySelectedCell != null) {
            // Check if it was a date cell (not empty) before resetting style
            if (!currentlySelectedCell.getStyle().equals(EMPTY_CELL_STYLE)) {
                currentlySelectedCell.setStyle(DATE_CELL_STYLE);
            }
            currentlySelectedCell = null;
        }
    }
}