package com.example.myapplication.activities.data;

public class ListData {

    private String descr;
    private String fecha;
    private String ingreso;
    private String gasto;
    private String id;
    private String categId;

    public ListData(String descr, String fecha, String ingreso, String gasto, String id, String categId) {
        this.categId = categId;
        this.id = id;
        this.descr = descr;
        this.fecha = fecha;
        this.ingreso = ingreso;
        this.gasto = gasto;
    }

    public String getCategId() {
        return categId;
    }

    public String getId() {
        return id;
    }

    public String getIngreso() {
        return ingreso;
    }

    public String getGasto() {
        return gasto;
    }

    public String getDescr() {
        return descr;
    }

    public String getFecha() {
        return fecha;
    }
}
