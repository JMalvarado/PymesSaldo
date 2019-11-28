package com.example.myapplication.activities.data;

/**
 * List data for card view of items in report fragment
 */
public class ListDataReport {

    private String id;
    private String name;
    private double percentage;
    private double amount;
    private String ic;

    public ListDataReport(String id, String name, double percentage, double amount, String ic) {
        this.id = id;
        this.name = name;
        this.percentage = percentage;
        this.amount = amount;
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

    public double getPercentage() {
        return percentage;
    }

    public double getAmount() {
        return amount;
    }
}
