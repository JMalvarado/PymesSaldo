package com.example.myapplication.activities.data;

/**
 * List data for card view of items in debt fragment
 */
public class ListDataDebt {

    private String id;
    private String descripcion;
    private String amount;
    private String date;

    public ListDataDebt(String id, String description, String amount, String date) {
        this.id = id;
        this.descripcion = description;
        this.amount = amount;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
