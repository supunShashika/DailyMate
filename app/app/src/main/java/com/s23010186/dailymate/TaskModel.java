package com.s23010186.dailymate;

public class TaskModel {
    int id;
    String title, deadline;

    public TaskModel(int id, String title, String deadline) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }
}