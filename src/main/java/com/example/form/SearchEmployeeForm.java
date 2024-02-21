package com.example.form;

public class SearchEmployeeForm {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SearchEmployeeForm [name=" + name + "]";
    }
    
}
