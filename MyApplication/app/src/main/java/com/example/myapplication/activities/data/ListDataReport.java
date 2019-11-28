package com.example.myapplication.activities.data;

/**
 * List data for card view of items in report fragment
 */
public class ListDataReport {

    private String id;
    private String name;
    private int percentage;
    private int ammount;
    private String ic;

    public ListDataReport(String id, String name, int percentage, int ammount, String ic) {
        this.id = id;
        this.name = name;
        this.percentage = percentage;
        this.ammount = ammount;
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

    public int getPercentage() {
        return percentage;
    }

    public int getAmmount() {
        return ammount;
    }
}
