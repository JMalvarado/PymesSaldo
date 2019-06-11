package com.pymes.pymessaldo;

public class ListData {

    private String descr;
    private String fecha;

    public ListData(String descr, String fecha) {
        this.descr = descr;
        this.fecha = fecha;
    }

    public String getDescr() {
        return descr;
    }

    public String getFecha() {
        return fecha;
    }
}
