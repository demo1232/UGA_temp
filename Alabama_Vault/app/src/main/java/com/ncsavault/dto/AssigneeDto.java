package com.ncsavault.dto;

/**
 * Class will hold the for api Assignee dto.
 */
@SuppressWarnings("unused")
public class AssigneeDto {
    // this is assignee id
    private long id;

    // this is assignee name
    private String name;


    /**
     * @return Gets the value of id and returns id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id
     * You can use getId() to get the value of id
     */
    public void setId(long id) {
        this.id = id;
    }

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


}
