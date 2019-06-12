package com.pymes.pymessaldo;

public class ListData {

    private String descr;
    private String fecha;
    private String ingreso;
    private String gasto;

    public ListData(String descr, String fecha, String ingreso, String gasto) {
        this.descr = descr;
        this.fecha = fecha;
        this.ingreso = ingreso;
        this.gasto = gasto;
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
