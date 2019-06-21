package com.example.myapplication.activities.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Saldos.db";

    //tabla1
    private static final String TABLA1_NOMBRE = "Saldos";
    //columnas
    private static final String Col_Fecha = "Fecha";
    private static final String Col_Ingreso = "Ingreso";
    private static final String Col_Gasto = "Gasto";
    private static final String Col_Descripcion = "Descripcion";
    private static final String Col_InstanciaID = "Instancias_ID";

    //tabla2
    private static final String TABLA2_NOMBRE = "Instancias";
    //columnas
    private static final String Col_Nombre = "Nombre";

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
        // Create table for instances
        sqLiteDatabase.execSQL("create table " + TABLA2_NOMBRE + " " +
                "(Instancias_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Nombre TEXT)");

        // Create table for entries
        sqLiteDatabase.execSQL("create table " + TABLA1_NOMBRE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Instancias_ID INTEGER," +
                "Fecha DATE," +
                "Ingreso INTEGER," +
                "Gasto INTEGER," +
                "Descripcion TEXT," +
                "FOREIGN KEY (Instancias_ID) REFERENCES Instancias (Instancias_ID) " +
                "ON DELETE CASCADE ON UPDATE NO ACTION)");
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA2_NOMBRE);
        onCreate(sqLiteDatabase);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Add methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Add an entry to table Saldos
     *
     * @param Date
     * @param gasto
     * @param ingreso
     * @param descripcion
     * @param instance_id
     * @return
     */
    public boolean addEntry(String Date, int gasto, int ingreso, String descripcion, String instance_id) {

        SQLiteDatabase db = this.getWritableDatabase(); //Obtiene la instancia de base de datos para ingresar datos.

        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_InstanciaID, instance_id);
        contentValues.put(Col_Fecha, Date);
        contentValues.put(Col_Ingreso, ingreso);
        contentValues.put(Col_Gasto, gasto);
        contentValues.put(Col_Descripcion, descripcion);

        //Ingresa datos.
        long resultado = db.insert(TABLA1_NOMBRE, null, contentValues);

        //Retorna el estado del resultado del metodo insert.
        return resultado != -1;
    }

    /**
     * Add an instance to table Instances
     *
     * @param name
     * @return
     */
    public boolean addInstance(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Nombre, name);

        long resultado = db.insert(TABLA2_NOMBRE, null, contentValues);

        return resultado != -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Get methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the instance name from table Instancias
     *
     * @param id
     * @return name of the instance
     */
    public String getInstanceName(String id) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA2_NOMBRE + " WHERE Instancias_ID="+id, null);

        String name = "";

        while (consulta.moveToNext()) {
            name = consulta.getString(1);
        }

        return name;
    }

    /**
     * Get instance id from database, with the given name parameter
     * @param name
     * @return
     */
    public String getInstanceId(String name) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA2_NOMBRE + " WHERE Nombre='"+name+"'", null);

        String id = "";

        while (consulta.moveToNext()) {
            id = consulta.getString(0);
        }

        return id;
    }

    /**
     * Get all the data from table Intancias
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getInstancesAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLA2_NOMBRE, null);
    }

    /**
     * Get all the data from Column Gastos
     * @param Instancias_ID ID of the current instance
     * @return
     */
    public ArrayList<Integer> getEntryGastos(String Instancias_ID) {
        //valores a usar.
        int gasto;

        //lista a retornar

        ArrayList<Integer> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID="+Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getInt(4);
            gastos.add(gasto);
        }

        return gastos;
    }

    /**
     * Get all the data from column Ingresos
     *
     * @return
     */
    public ArrayList<Integer> getEntryIngresos(String Instancias_ID) {
        //valores a usar.
        int ingreso;

        //lista a retornar

        ArrayList<Integer> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE+" WHERE Instancias_ID="+Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getInt(3);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get the data from column ingresos in the actual month
     *
     * @return
     */
    public ArrayList<Integer> getEntryCurrentMonthIngresos(String Instancias_ID) {
        //valores a usar.
        int ingreso;

        //lista a retornar

        ArrayList<Integer> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                        "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID="+Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getInt(3);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get the data from column Gasto in the current month
     *
     * @return
     */
    public ArrayList<Integer> getEntryCurrentMonthGastos(String Instancias_ID) {
        //valores a usar.
        int gasto;

        //lista a retornar

        ArrayList<Integer> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                        "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID="+Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getInt(4);
            gastos.add(gasto);
        }

        return gastos;
    }

    /**
     * Get all the data from table Saldos
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryAllData(String Instancias_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE+" WHERE Instancias_ID="+Instancias_ID, null);
        return resultado;
    }

    /**
     * Get the data from table Saldos in the actual month
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryMonthData(String Instancias_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID="+Instancias_ID, null);
        return resultado;
    }

    /**
     * Get the data from table Saldos from beginning to a given date
     *
     * @param finalDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryDataFromBegToDate(String Instancias_ID, String finalDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha <= '" + finalDate + "' AND Instancias_ID="+Instancias_ID, null);
        return resultado;
    }

    /**
     * Get all the data from table Saldos from a given date to the end of times
     *
     * @param beginDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryDataFromDateToToday(String Instancias_ID, String beginDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha >= '" + beginDate + "' AND Instancias_ID="+Instancias_ID, null);
        return resultado;
    }

    /**
     * Get all the data from table Saldos between two given dates
     *
     * @param beginDate
     * @param finalDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryDataInDate(String Instancias_ID, String beginDate, String finalDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' AND '" + finalDate + "') AND Instancias_ID="+Instancias_ID, null);
        return resultado;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Delete methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Delete a row from table Saldos with the given id
     *
     * @param id
     * @return the number of rows successfully deleted
     */
    public Integer deleteEntryData(String Instancias_ID, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA1_NOMBRE, "ID = ? AND Instancias_ID=?", new String[]{id,Instancias_ID});
    }

    /**
     * Delete a row from table Instancias given by the instance_ID
     * @param Instancias_ID
     * @return
     */
    public Integer deleteInstance(String Instancias_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA2_NOMBRE, "Instancias_Id=?", new String[]{Instancias_ID});
    }

    /**
     * Edit an entry with the given id and instancia id
     * @param id
     * @param date
     * @param ingreso
     * @param gasto
     * @param descr
     * @param inst_ID
     * @return
     */
    public boolean editEntryData(String id, String date, String ingreso, String gasto, String descr, String inst_ID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put(Col_InstanciaID, inst_ID);
        contentValues.put(Col_Fecha, date);
        contentValues.put(Col_Ingreso, ingreso);
        contentValues.put(Col_Gasto, gasto);
        contentValues.put(Col_Descripcion, descr);

        db.update(TABLA1_NOMBRE, contentValues, "ID=? AND Instancias_ID=?", new String[]{id, inst_ID});

        return true;
    }

    /**
     * Edit an instance with the given id instance
     * @param instance_ID
     * @param name
     * @return
     */
    public boolean editInstance(String instance_ID, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Instancias_ID", instance_ID);
        contentValues.put(Col_Nombre, name);

        db.update(TABLA2_NOMBRE, contentValues, "Instancias_ID=?", new String[]{instance_ID});

        return true;
    }
}