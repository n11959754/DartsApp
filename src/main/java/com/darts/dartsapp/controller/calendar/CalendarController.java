package com.darts.dartsapp.controller.calendar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CalendarController {

    // FXML Fields
    @FXML private Label monthYearLabel;
    //@FXML private Button prevMonthButton;
    //@FXML private Button nextMonthButton;
    @FXML private GridPane calendarGrid;

    // colours for the calendar
    private static final String DATE_CELL_STYLE = "-fx-background-color: #dae3eb; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String EMPTY_CELL_STYLE = "-fx-background-color: #ECEFF1; -fx-border-color: #B0BEC5; -fx-border-width: 1px;";
    private static final String SELECTED_CELL_STYLE = "-fx-background-color: #2F638F; -fx-border-color: #0288D1; -fx-border-width: 1px;";

    // controller State
    private YearMonth currentYearMonth;
    private Node currentlySelectedCell = null;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy"); // Corrected pattern

    // initialises the controller class automatically called after fxml file has been loaded
    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        drawCalendar();
    }

    // previous month button
    @FXML
    private void handlePreviousMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.minusMonths(1);
        clearSelection();
        drawCalendar();
    }

    // next month button
    @FXML
    private void handleNextMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        clearSelection();
        drawCalendar();
    }

    // updates the calendar view (label and grid)
    private void drawCalendar() {
        //updates the month/year label
        monthYearLabel.setText(currentYearMonth.format(monthYearFormatter).toUpperCase());
        // populates the grid with days
        populateCalendarGrid();
    }

    // populates the calendar grid with the date for currently selected month
    private void populateCalendarGrid() {
        //System.out.println("Drawing calendar for: " + currentYearMonth);

        if (calendarGrid != null) {
            // clear previous months cells (only those added in rows > 0)
            calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        } else {
            System.err.println("Error");
            return;
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() - 1; // 0=Mon, 1=Tue, ..., 6=Sun

        int daysInMonth = currentYearMonth.lengthOfMonth();

        // creates and add date cells (StackPanes with Labels)
        int currentDay = 1;
        for (int row = 1; row <= 6; row++) { // goes through the weeks
            int col;
            for (col = 0; col < 7; col++) { // goes through days mon-sun
                StackPane cell;
                if ((row == 1 && col < dayOfWeekOffset) || currentDay > daysInMonth) {
                    // cell before the first day or after the last day of the month
                    cell = createEmptyCell();
                } else {
                    cell = createDateCell(currentDay);
                    currentDay++;
                }
                calendarGrid.add(cell, col, row);
            }
        }
    }

    // creates a stackpane for a date cell
    private StackPane createDateCell(int day) {
        StackPane cell = new StackPane();
        cell.setOnMouseClicked(this::handleDateCellClick);
        cell.setStyle(DATE_CELL_STYLE);
        Label label = new Label(String.valueOf(day));
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        // aligns label to top right within the stackpane
        StackPane.setAlignment(label, Pos.TOP_RIGHT);

        cell.getChildren().add(label);
        cell.setPadding(new Insets(5));
        return cell;
    }

    // creates empty non-interactive cell
    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.setPadding(new Insets(5));
        cell.setMouseTransparent(true);
        cell.setStyle(EMPTY_CELL_STYLE);
        return cell;
    }

    // handles clicks on date cells
    @FXML
    private void handleDateCellClick(MouseEvent event) {
        Node clickedNode = (Node) event.getSource();
        if (!(clickedNode instanceof StackPane clickedCell)) { return; }
        if (clickedCell.getChildren().isEmpty() || !(clickedCell.getChildren().getFirst() instanceof Label label)) { clearSelection(); return; }
        if (label.getText() == null || label.getText().trim().isEmpty()) { clearSelection(); return; }

        // if selected cell is clicked again the colour is reset
        if (clickedNode == currentlySelectedCell) {
            clickedNode.setStyle(DATE_CELL_STYLE);
            return;
        }

        // select the new cell
        clickedNode.setStyle(SELECTED_CELL_STYLE);
        currentlySelectedCell = clickedNode; // tracks the selected node
    }

    // removes the selection highlight / visual feedback
    private void clearSelection() {
        if (currentlySelectedCell != null) {
            currentlySelectedCell = null;
        }
    }

    // other methods
    @FXML
    private void handleProfileImageClick(ActionEvent event) {
        //System.out.println("Profile image clicked!");
        // implement stuff...
    }
}