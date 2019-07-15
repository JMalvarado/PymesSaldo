package com.example.myapplication.activities.data;

public class ListDataCategory {

    private String id;
    private String name;
    private String ic;

    public ListDataCategory(String id, String name, String ic) {
        this.id = id;
        this.name = name;
        this.ic = ic;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIc() {
        return ic;
    }
}
