package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.*;

public class DatabaseController {

    private final UserTable users;
    private final ClassTable classes;
    private final AssignmentsTable assignments;
    private final ClassTimeSlotTable timeSlots;
    private final SettingsTable settings;

    public DatabaseController() {
        this.users = new UserTable();
        this.classes = new ClassTable();
        this.assignments = new AssignmentsTable();
        this.timeSlots = new ClassTimeSlotTable();
        this.settings = new SettingsTable();
    }

    // Getter methods with corrected naming convention
    public UserTable getUsersTable() {
        return users;
    }

    public ClassTable getClassTable() {
        return classes;
    }

    public AssignmentsTable getAssignmentsTable() {
        return assignments;
    }

    public ClassTimeSlotTable getTimeSlotsTable() {
        return timeSlots;
    }

    public SettingsTable getSettingsTable() {
        return settings;
    }
}
