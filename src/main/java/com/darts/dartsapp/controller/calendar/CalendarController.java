package com.darts.dartsapp.controller.calendar;

import com.darts.dartsapp.model.ClassTable;
import com.darts.dartsapp.model.ClassTimeSlot; // Import the ClassTimeSlot model
import com.darts.dartsapp.model.ClassTimeSlotTable;
import com.darts.dartsapp.model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.Comparator; // Added for sorting events
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarController {

    // FXML Fields
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;

    // styles for the calendar cells //blue: 2F638F
    private static final String DATE_CELL_STYLE = "-fx-background-color: #dae3eb; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String EMPTY_CELL_STYLE = "-fx-background-color: #ECEFF1; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String SELECTED_CELL_STYLE = "-fx-background-color: #2F638F; -fx-border-color: #0288D1; -fx-border-width: 1px;";

    // controller state
    private YearMonth currentYearMonth;
    private Node currentlySelectedCell = null;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    // class data structure
    private static class Event {
        String time;
        String name;
        String style;

        public Event(String time, String name, String style) {
            this.time = time;
            this.name = name;
            this.style = style;
        }
    }
    private final Map<DayOfWeek, List<Event>> weeklyEvents = new HashMap<>();

    // starts the controller class
    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        loadWeeklyEvents();
        drawCalendar();
    }

    // database interaction for classes //
    private List<ClassTimeSlot> fetchClassTimeSlotsFromDB() {
        List<ClassTimeSlot> allSlots = new ArrayList<>();

        if (!Session.isLoggedIn()) return allSlots;

        int userID = Session.getCurrentUser().getUserID();
        ClassTable classTable = new ClassTable();
        ClassTimeSlotTable timeSlotTable = new ClassTimeSlotTable();
        List<com.darts.dartsapp.model.Class> userClasses = classTable.getClassesByUser(userID);

        for (com.darts.dartsapp.model.Class userClass : userClasses) {
            int classID = userClass.getClassID();
            List<ClassTimeSlot> timeSlots = timeSlotTable.getTimeSlotsByClassID(classID);
            allSlots.addAll(timeSlots);
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

    private String determineTextColor(String backgroundColorHex) {
        if (backgroundColorHex == null || !backgroundColorHex.startsWith("#") ||
                (backgroundColorHex.length() != 7 && backgroundColorHex.length() != 9)) { // either #RRGGBB and or #RRGGBBAA
            return "black";
        }
        try {
            Color color = Color.web(backgroundColorHex);
            // 0.299*R + 0.587*G + 0.114*B (values are 0-1)
            double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
            return luminance > 0.5 ? "black" : "white";
        } catch (Exception e) {
            System.err.println("Error determining text color for background: " + backgroundColorHex + " - " + e.getMessage());
            return "black";
        }
    }

    // classes logic
    private void loadWeeklyEvents() {
        weeklyEvents.clear(); // clear any existing events
        List<ClassTimeSlot> classSlots = fetchClassTimeSlotsFromDB();

        for (ClassTimeSlot slot : classSlots) {
            DayOfWeek dayOfWeek = mapStringToDayOfWeek(slot.getDay());
            if (dayOfWeek == null) {
                System.err.println("Warning: Could not parse day: '" + slot.getDay() + "' for classID: " + slot.getClassID());
                continue; // skip the slot if day is invalid
            }
            String eventName = "ID:" + slot.getClassID() + " (" + slot.getType() + ")";
            String slotColour = slot.getColour() != null ? slot.getColour() : "#CCCCCC";
            String textColor = determineTextColor(slotColour);
            String eventStyle = "-fx-background-color: " + slotColour + "; -fx-text-fill: " + textColor + ";";

            weeklyEvents.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                    .add(new Event(slot.getTime(), eventName, eventStyle));
        }

        // sort events by time for each day
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
        drawCalendar();
    }

    private void drawCalendar() {
        monthYearLabel.setText(currentYearMonth.format(monthYearFormatter).toUpperCase());
        populateCalendarGrid();
    }

    private void populateCalendarGrid() {
        if (calendarGrid == null) {
            System.err.println("Error: calendarGrid is null in populateCalendarGrid. Cannot draw calendar.");
            return;
        }
        // clears previous months cells
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday is 0 Monday is 1 etc
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int currentDay = 1;

        for (int row = 1; row <= 6; row++) { // start from row 1 for dates
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
    // date cell
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
                eventLabel.setMaxWidth(Double.MAX_VALUE);
                VBox.setMargin(eventLabel, new Insets(1, 0, 0, 0));
                contentBox.getChildren().add(eventLabel);
            }
        }
        cell.getChildren().add(contentBox);
        return cell;
    }

    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.setPadding(new Insets(5));
        cell.setMouseTransparent(true); // Not clickable
        cell.setStyle(EMPTY_CELL_STYLE);
        return cell;
    }

    @FXML
    private void handleDateCellClick(MouseEvent event) {
        Node clickedNode = (Node) event.getSource();
        if (!(clickedNode instanceof StackPane)) { return; }

        // reset style of previously selected cell if it's not an empty cell
        if (currentlySelectedCell != null && currentlySelectedCell != clickedNode) {
            if (!currentlySelectedCell.getStyle().equals(EMPTY_CELL_STYLE)) {
                currentlySelectedCell.setStyle(DATE_CELL_STYLE);
            }
        }

        // applies selected style if it's a date cell
        if (!clickedNode.getStyle().equals(EMPTY_CELL_STYLE)) {
            clickedNode.setStyle(SELECTED_CELL_STYLE);
            currentlySelectedCell = clickedNode;
        } else {
            // ff an empty cell clicked and previously was selected clears the selection
            if (currentlySelectedCell == clickedNode) currentlySelectedCell = null;
        }
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