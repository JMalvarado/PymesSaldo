package com.example.myapplication.activities.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.R;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;
    private static final String DATABASE_NAME = "Saldos.db";

    //tabla1
    private static final String TABLA1_NOMBRE = "Saldos";
    //columnas
    private static final String Col_Fecha = "Fecha";
    private static final String Col_Hora = "Hora";
    private static final String Col_Ingreso = "Ingreso";
    private static final String Col_Gasto = "Gasto";
    private static final String Col_Descripcion = "Descripcion";
    private static final String Col_InstanciaID = "Instancias_ID";
    private static final String Col_CategID = "Categorias_ID";

    //tabla2
    private static final String TABLA2_NOMBRE = "Instancias";
    //columnas
    private static final String Col_Nombre = "Nombre";
    private static final String Col_Periodo = "Periodo";

    //tabla3
    private static final String TABLA3_NOMBRE = "Categorias";
    //columnas
    private static final String Col_NombreCateg = "Nombre";

    //tabla4
    private static final String TABLA4_NOMBRE = "Ahorro";
    //columnas
    private static final String Col_InstIDAhorro = "Instancias_ID";
    private static final String Col_MontoAhorro = "Monto";
    private static final String Col_FechaAhorro = "Fecha";
    private static final String Col_HoraAhorro = "Hora";
    private static final String Col_TipoMovAhorro = "Tipo";

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
                "Nombre TEXT," +
                "Periodo INTEGER)");

        // Create table for categories
        sqLiteDatabase.execSQL("create table " + TABLA3_NOMBRE + " " +
                "(Categorias_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Nombre TEXT)");

        // Create table for entries
        sqLiteDatabase.execSQL("create table " + TABLA1_NOMBRE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Instancias_ID INTEGER," +
                "Categorias_ID INTEGER," +
                "Fecha DATE," +
                "Hora TIME, " +
                "Ingreso INTEGER," +
                "Gasto INTEGER," +
                "Descripcion TEXT," +
                "FOREIGN KEY (Instancias_ID) REFERENCES Instancias (Instancias_ID) " +
                "ON DELETE CASCADE ON UPDATE NO ACTION," +
                "FOREIGN KEY (Categorias_ID) REFERENCES Categorias (Categorias_ID) " +
                "ON DELETE CASCADE ON UPDATE NO ACTION)");

        // Create table for Saving
        sqLiteDatabase.execSQL("create table " + TABLA4_NOMBRE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Instancias_ID INTEGER," +
                "Monto INTEGER," +
                "Fecha DATE," +
                "Hora TIME, " +
                "Tipo TEXT," +
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA3_NOMBRE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA4_NOMBRE);
        onCreate(sqLiteDatabase);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Add methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Add an entry to table Saldos
     *
     * @param Date
     * @param hora
     * @param gasto
     * @param ingreso
     * @param descripcion
     * @param instance_id
     * @return
     */
    public boolean addEntry(String Date, String hora, long gasto, long ingreso, String descripcion, String instance_id, String categ_id) {

        SQLiteDatabase db = this.getWritableDatabase(); //Obtiene la instancia de base de datos para ingresar datos.

        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_InstanciaID, instance_id);
        contentValues.put(Col_CategID, categ_id);
        contentValues.put(Col_Fecha, Date);
        contentValues.put(Col_Hora, hora);
        contentValues.put(Col_Ingreso, ingreso);
        contentValues.put(Col_Gasto, gasto);
        contentValues.put(Col_Descripcion, descripcion);

        //Ingresa datos.
        long resultado = db.insert(TABLA1_NOMBRE, null, contentValues);

        //Retorna el estado del resultado del metodo insert.
        return resultado != -1;
    }

    /**
     * Add a category to table Categorias
     *
     * @param name
     * @return
     */
    public boolean addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_NombreCateg, name);

        long resultado = db.insert(TABLA3_NOMBRE, null, contentValues);

        return resultado != -1;
    }

    /**
     * Add an instance to table Instances
     *
     * @param name
     * @return
     */
    public boolean addInstance(String name, int period) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Nombre, name);
        contentValues.put(Col_Periodo, period);

        long resultado = db.insert(TABLA2_NOMBRE, null, contentValues);

        return resultado != -1;
    }

    /**
     * Add new saving data to table "Ahorro"
     *
     * @param instanceID
     * @param value
     * @param date
     * @param time
     * @param type
     * @return
     */
    public boolean addSaving(String instanceID, long value, String date, String time, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_InstIDAhorro, instanceID);
        contentValues.put(Col_MontoAhorro, value);
        contentValues.put(Col_FechaAhorro, date);
        contentValues.put(Col_HoraAhorro, time);
        contentValues.put(Col_TipoMovAhorro, type);

        long resultado = db.insert(TABLA4_NOMBRE, null, contentValues);

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
                "SELECT * FROM " + TABLA2_NOMBRE + " WHERE Instancias_ID=" + id, null);

        String name = "";

        while (consulta.moveToNext()) {
            name = consulta.getString(1);
        }

        return name;
    }

    /**
     * Get instance id from database, with the given name parameter
     *
     * @param name
     * @return
     */
    public String getInstanceId(String name) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA2_NOMBRE + " WHERE Nombre='" + name + "'", null);

        String id = "";

        while (consulta.moveToNext()) {
            id = consulta.getString(0);
        }

        return id;
    }

    /**
     * Get period day from instance table
     * @param name
     * @return
     */
    public int getInstancePeriod(String name) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA2_NOMBRE + " WHERE Nombre='" + name + "'", null);

        int period = 0;

        while (consulta.moveToNext()) {
            period = consulta.getInt(2);
        }

        return period;
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
     * Get category id from database, with the given name parameter
     *
     * @param name
     * @return
     */
    public String getCategoryId(String name) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA3_NOMBRE + " WHERE Nombre='" + name + "'", null);

        String id = "";

        while (consulta.moveToNext()) {
            id = consulta.getString(0);
        }

        return id;
    }

    /**
     * Get category name from database, with the given id parameter
     *
     * @param id
     * @return
     */
    public String getCategoryName(String id) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA3_NOMBRE + " WHERE Categorias_ID='" + id + "'", null);

        String name = "";

        while (consulta.moveToNext()) {
            name = consulta.getString(1);
        }

        return name;
    }


    /**
     * Get all the data from table Categorias
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getCategoryAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLA3_NOMBRE, null);
    }

    /**
     * Get all the data from Column Gastos
     *
     * @param Instancias_ID ID of the current instance
     * @return
     */
    public ArrayList<Long> getEntryGastos(String Instancias_ID) {
        //valores a usar.
        long gasto;

        //lista a retornar

        ArrayList<Long> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getLong(6);
            gastos.add(gasto);
        }

        return gastos;
    }

    /**
     * Get all the data from column Ingresos
     *
     * @return
     */
    public ArrayList<Long> getEntryIngresos(String Instancias_ID) {
        //valores a usar.
        long ingreso;

        //lista a retornar

        ArrayList<Long> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getLong(5);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get the data from column ingresos in the actual month
     *
     * @return
     */
    public ArrayList<Long> getEntryCurrentMonthIngresos(String Instancias_ID) {
        //valores a usar.
        long ingreso;

        //lista a retornar

        ArrayList<Long> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                        "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getLong(5);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get the data from column ingresos in the given month and year
     *
     * @return
     */
    public ArrayList<Long> getEntryInMonthYearIngresos(String Instancias_ID, String month, String year) {
        //valores a usar.
        long ingreso;

        //lista a retornar

        ArrayList<Long> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                        "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getLong(5);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get the data from column Gasto in the current month
     *
     * @return
     */
    public ArrayList<Long> getEntryCurrentMonthGastos(String Instancias_ID) {
        //valores a usar.
        long gasto;

        //lista a retornar

        ArrayList<Long> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                        "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getLong(6);
            gastos.add(gasto);
        }

        return gastos;
    }

    /**
     * Get the data from column Gasto in the given month and year
     *
     * @return
     */
    public ArrayList<Long> getEntryInMonthYearGastos(String Instancias_ID, String month, String year) {
        //valores a usar.
        long gasto;

        //lista a retornar

        ArrayList<Long> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                        "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getLong(6);
            gastos.add(gasto);
        }

        return gastos;
    }

    /**
     * Get all the data from table Saldos
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryAllData(String Instancias_ID, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID, null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category, null);
        }

        return resultado;
    }

    /**
     * Get the data from table Saldos in the actual month
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryMonthData(String Instancias_ID, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                    "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID, null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                    "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category, null);
        }

        return resultado;
    }

    /**
     * Get the data from table Saldos from beginning to a given date
     *
     * @param finalDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryDataFromBegToDate(String Instancias_ID, String finalDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha <= '" + finalDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID, null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha <= '" + finalDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category, null);
        }
        return resultado;
    }

    /**
     * Get all the data from table Saldos from a given date to the end of times
     *
     * @param beginDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryDataFromDateToToday(String Instancias_ID, String beginDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha >= '" + beginDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID, null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha >= '" + beginDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category, null);
        }
        return resultado;
    }

    /**
     * Get all the data from table Saldos between two given dates
     *
     * @param beginDate
     * @param finalDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryDataInDate(String Instancias_ID, String beginDate, String finalDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' " +
                    "AND '" + finalDate + "') AND Instancias_ID=" + Instancias_ID, null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' " +
                    "AND '" + finalDate + "') AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category, null);
        }
        return resultado;
    }

    /**
     * Get the data from Ahorro of type "A"
     *
     * @return
     */
    public ArrayList<Long> getSaveAllPayment(String Instancias_ID) {
        //valores a usar.
        long abono;

        //lista a retornar

        ArrayList<Long> abonos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA4_NOMBRE + " WHERE Tipo='A' AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            abono = consulta.getLong(2);
            abonos.add(abono);
        }

        return abonos;
    }

    /**
     * Get the data from Ahorro of type "R"
     *
     * @return
     */
    public ArrayList<Long> getSaveAllWithdrawal(String Instancias_ID) {
        //valores a usar.
        long retiro;

        //lista a retornar

        ArrayList<Long> retiros = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA4_NOMBRE + " WHERE Tipo='R' AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            retiro = consulta.getLong(2);
            retiros.add(retiro);
        }

        return retiros;
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
        return db.delete(TABLA1_NOMBRE, "ID = ? AND Instancias_ID=?", new String[]{id, Instancias_ID});
    }

    /**
     * Delete a row from table Instancias given by the instance_ID
     *
     * @param Instancias_ID
     * @return
     */
    public Integer deleteInstance(String Instancias_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA2_NOMBRE, "Instancias_Id=?", new String[]{Instancias_ID});
    }

    /**
     * Edit an entry with the given id and instancia id
     *
     * @param id
     * @param date
     * @param ingreso
     * @param gasto
     * @param descr
     * @param inst_ID
     * @return
     */
    public boolean editEntryData(String id, String date, String hora, String ingreso, String gasto, String descr, String inst_ID, String new_inst_ID, String categ_ID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put(Col_InstanciaID, new_inst_ID);
        contentValues.put(Col_CategID, categ_ID);
        contentValues.put(Col_Fecha, date);
        contentValues.put(Col_Hora, hora);
        contentValues.put(Col_Ingreso, ingreso);
        contentValues.put(Col_Gasto, gasto);
        contentValues.put(Col_Descripcion, descr);

        db.update(TABLA1_NOMBRE, contentValues, "ID=? AND Instancias_ID=?", new String[]{id, inst_ID});

        return true;
    }

    /**
     * Edit an instance with the given id instance
     *
     * @param instance_ID
     * @param name
     * @return
     */
    public boolean editInstance(String instance_ID, String name, int period) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Instancias_ID", instance_ID);
        contentValues.put(Col_Nombre, name);
        contentValues.put(Col_Periodo, period);

        db.update(TABLA2_NOMBRE, contentValues, "Instancias_ID=?", new String[]{instance_ID});

        return true;
    }
}