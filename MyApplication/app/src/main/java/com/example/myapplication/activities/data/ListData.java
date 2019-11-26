package com.example.myapplication.activities.data;

/**
 * List data for card view of items in search activity
 */
public class ListData {

    private String descr;
    private String fecha;
    private String hora;
    private String ingreso;
    private String gasto;
    private String id;
    private String categId;

    public ListData(String descr, String fecha, String hora, String ingreso, String gasto, String id, String categId) {
        this.categId = categId;
        this.id = id;
        this.descr = descr;
        this.fecha = fecha;
        this.hora = hora;
        this.ingreso = ingreso;
        this.gasto = gasto;
    }

    public String getHora() {
        return hora;
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
