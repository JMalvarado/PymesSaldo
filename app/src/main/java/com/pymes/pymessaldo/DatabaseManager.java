package com.pymes.pymessaldo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Saldos.db";

    //tabla
    private static final String TABLA1_NOMBRE = "Saldos";
    //columnas
    private static final String Col_Fecha = "Fecha";
    private static final String Col_Ingreso = "Ingreso";
    private static final String Col_Gasto = "Gasto";

    //constructor

    public DatabaseManager(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Metodo OnCreate para crear las tablas.
     *
     * @param sqLiteDatabase Base de datos usada.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Crear tabla Usuarios
        sqLiteDatabase.execSQL("create table " + TABLA1_NOMBRE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Fecha DATE," +
                "Ingreso INTEGER," +
                "Gasto INTEGER)");
    }

    /**
     * Metodo de mejora y actualizacion de la base de datos.
     *
     * @param sqLiteDatabase Base de datos usada.
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA1_NOMBRE);
        onCreate(sqLiteDatabase);
    }


    public boolean addEntry(String Date, int gasto, int ingreso) {

        SQLiteDatabase db = this.getWritableDatabase(); //Obtiene la instancia de base de datos para ingresar datos.

        ContentValues contentValues = new ContentValues(); //Almacena una serie de datos.
        contentValues.put(Col_Fecha, Date); //Almacena clave-valor para la tabla.
        contentValues.put(Col_Ingreso, ingreso); //Almacena clave-valor para la tabla.
        contentValues.put(Col_Gasto, gasto); //Almacena clave-valor para la tabla.

        //Ingresa datos.
        long resultado = db.insert(TABLA1_NOMBRE, null, contentValues);

        //Retorna el estado del resultado del metodo insert.
        return resultado != -1;
    }

    public ArrayList<Integer> getGastos() {
        //valores a usar.
        int gasto;

        //lista a retornar

        ArrayList<Integer> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getInt(3);
            gastos.add(gasto);
        }

        return gastos;
    }

    public ArrayList<Integer> getIngresos() {
        //valores a usar.
        int ingreso;

        //lista a retornar

        ArrayList<Integer> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getInt(2);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    public ArrayList<Integer> getCurrentMonthIngresos() {
        //valores a usar.
        int ingreso;

        //lista a retornar

        ArrayList<Integer> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                        "AND strftime('%m',Fecha)=strftime('%m',date('now'))", null);
        //  where strftime('%Y',Fecha)=strftime('%Y',date('now')) AND strftime('%m',Fecha)=strftime('%m',date('now'));

        while (consulta.moveToNext()) {
            ingreso = consulta.getInt(2);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    public ArrayList<Integer> getCurrentMonthGastos() {
        //valores a usar.
        int gasto;

        //lista a retornar

        ArrayList<Integer> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                        "AND strftime('%m',Fecha)=strftime('%m',date('now'))", null);
        //  where strftime('%Y',Fecha)=strftime('%Y',date('now')) AND strftime('%m',Fecha)=strftime('%m',date('now'));

        while (consulta.moveToNext()) {
            gasto = consulta.getInt(3);
            gastos.add(gasto);
        }

        return gastos;
    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor resultado=db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                "AND strftime('%m',Fecha)=strftime('%m',date('now'))", null);
        return resultado;
    }
}

