package com.darts.dartsapp.controller;
import com.darts.dartsapp.model.*;

public class DatabaseController {

    private UserTable users;
    private ClassTable classes;
    private AssignmentsTable assignments;
    private ClassTimeSlotTable timeSlots;
    private Settings settings

    public DatabaseController () {
        this.users = new UserTable();
        this.classes = new ClassTable();
        this.assignments = new AssignmentsTable();
        this.timeSlots = new ClassTimeSlotTable();
        this.settings = new SettingsTable()

    }

    public UserTable usersTable() { return users; }
    public ClassTable classTable() { return classes; }
    public AssignmentsTable assignmentsTable() { return assignments; }
    public ClassTimeSlotTable timeSlotsTable() { return timeSlots; }
    public SettingsTable settings() { return settings; }
}
