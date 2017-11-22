package com.ncsavault.dto;

import java.util.ArrayList;

/**
 * Class will hold the data on server for asana ticket generating.
 */
@SuppressWarnings("unused")
public class  TaskDto {

    //this is task name
    private String name;

    //this is task notes
    private String notes;

    //this is task status
    private String assignee_status;

    //this is assignee dto
    private AssigneeDto assignee;

    //this is project name
    private String projects;

    //this is task workspace
    private long workspace;

    /**
     * @return Gets the value of name and returns name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     * You can use getName() to get the value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Gets the value of notes and returns notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes
     * You can use getNotes() to get the value of notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return Gets the value of assignee_status and returns assignee_status
     */
    public String getAssignee_status() {
        return assignee_status;
    }

    /**
     * Sets the assignee_status
     * You can use getAssignee_status() to get the value of assignee_status
     */
    public void setAssignee_status(String assignee_status) {
        this.assignee_status = assignee_status;
    }

    /**
     * @return Gets the value of assignee and returns assignee
     */
    public AssigneeDto getAssignee() {
        return assignee;
    }

    /**
     * Sets the assignee
     * You can use getAssignee() to get the value of assignee
     */
    public void setAssignee(AssigneeDto assignee) {
        this.assignee = assignee;
    }

    /**
     * @return Gets the value of projects and returns projects
     */
    public String getProjects() {
        return projects;
    }

    /**
     * Sets the projects
     * You can use getProjects() to get the value of projects
     */
    public void setProjects(String projects) {
        this.projects = projects;
    }

    /**
     * @return Gets the value of workspace and returns workspace
     */
    public long getWorkspace() {
        return workspace;
    }

    /**
     * Sets the workspace
     * You can use getWorkspace() to get the value of workspace
     */
    public void setWorkspace(long workspace) {
        this.workspace = workspace;
    }




}
