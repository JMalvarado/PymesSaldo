package com.example.myapplication.activities.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Database manager
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 19;
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
    private static final String Col_InstIDCateg = "Instancias_ID";
    private static final String Col_NombreCateg = "Nombre";
    private static final String Col_Icono = "Icono";

    //tabla4
    private static final String TABLA4_NOMBRE = "Ahorro";
    //columnas
    private static final String Col_InstIDAhorro = "Instancias_ID";
    private static final String Col_MontoAhorro = "Monto";
    private static final String Col_FechaAhorro = "Fecha";
    private static final String Col_HoraAhorro = "Hora";
    private static final String Col_TipoMovAhorro = "Tipo";

    //tabla5
    private static final String TABLA5_NOMBRE = "Deudas";
    //columnas
    private static final String Col_InstIDDeuda = "Instancias_ID";
    private static final String Col_DescripcionDeuda = "Descripcion";
    private static final String Col_MontoDeuda = "Monto";
    private static final String Col_FechaDeuda = "Fecha";

    //constructor
    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");
        super.onOpen(db);
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
                "Nombre TEXT," +
                "Icono TEXT," +
                "Instancias_ID INTEGER," +
                "FOREIGN KEY (Instancias_ID) REFERENCES Instancias (Instancias_ID) " +
                "ON DELETE CASCADE ON UPDATE NO ACTION)");

        // Create table for entries
        sqLiteDatabase.execSQL("create table " + TABLA1_NOMBRE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Instancias_ID INTEGER," +
                "Categorias_ID INTEGER," +
                "Fecha DATE," +
                "Hora TIME, " +
                "Ingreso REAL," +
                "Gasto REAL," +
                "Descripcion TEXT," +
                "FOREIGN KEY (Instancias_ID) REFERENCES Instancias (Instancias_ID) " +
                "ON DELETE CASCADE ON UPDATE NO ACTION," +
                "FOREIGN KEY (Categorias_ID) REFERENCES Categorias (Categorias_ID) " +
                "ON DELETE CASCADE ON UPDATE NO ACTION)");

        // Create table for Saving
        sqLiteDatabase.execSQL("create table " + TABLA4_NOMBRE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Instancias_ID INTEGER," +
                "Monto REAL," +
                "Fecha DATE," +
                "Hora TIME, " +
                "Tipo TEXT," +
                "FOREIGN KEY (Instancias_ID) REFERENCES Instancias (Instancias_ID) " +
                "ON DELETE CASCADE ON UPDATE NO ACTION)");

        // Create table for Dept
        sqLiteDatabase.execSQL("create table " + TABLA5_NOMBRE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Instancias_ID INTEGER," +
                "Descripcion TEXT," +
                "Monto REAL," +
                "Fecha DATE," +
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA5_NOMBRE);
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
    public boolean addEntry(String Date, String hora, double gasto, double ingreso, String descripcion, String instance_id, String categ_id) {

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
     * @param name category name
     * @param icon icon name
     * @return process state
     */
    public boolean addCategory(String name, String icon, String instance_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_InstIDCateg, instance_id);
        contentValues.put(Col_NombreCateg, name);
        contentValues.put(Col_Icono, icon);

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
    public boolean addSaving(String instanceID, double value, String date, String time, String type) {
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

    /**
     * Add new dept to table "Deudas"
     *
     * @param instanceID
     * @param value
     * @param date
     * @return
     */
    public boolean addDept(String instanceID, String descripcion, double value, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_InstIDDeuda, instanceID);
        contentValues.put(Col_DescripcionDeuda, descripcion);
        contentValues.put(Col_MontoDeuda, value);
        contentValues.put(Col_FechaAhorro, date);

        long resultado = db.insert(TABLA5_NOMBRE, null, contentValues);

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
     *
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
    public String getCategoryId(String name, String instance_id) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA3_NOMBRE + " WHERE Nombre='" + name + "' " +
                        "AND Instancias_ID=" + instance_id, null);

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
    public String getCategoryName(String id, String instance_id) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA3_NOMBRE + " WHERE Categorias_ID='" + id + "' " +
                        "AND Instancias_ID=" + instance_id, null);

        String name = "";

        while (consulta.moveToNext()) {
            name = consulta.getString(1);
        }

        return name;
    }

    /**
     * Get the category icon name
     *
     * @param name category's name
     * @return icon's name
     */
    public String getCategoryIconName(String name, String instance_id) {
        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA3_NOMBRE + " WHERE Nombre='" + name + "' " +
                        "AND Instancias_ID=" + instance_id, null);

        String icon = "";

        while (consulta.moveToNext()) {
            icon = consulta.getString(2);
        }

        return icon;
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
     * Get all the categories of specific instance
     *
     * @param instance_id
     * @return
     */
    public Cursor getCategoriesByInstance(String instance_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLA3_NOMBRE +
                " WHERE Instancias_ID=" + instance_id, null);
    }

    /**
     * Get all the data from Column Gastos
     *
     * @param Instancias_ID ID of the current instance
     * @return
     */
    public ArrayList<Double> getEntryGastos(String Instancias_ID) {
        //valores a usar.
        double gasto;

        //lista a retornar

        ArrayList<Double> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getDouble(6);
            gastos.add(gasto);
        }

        return gastos;
    }

    /**
     * Get all the data from column Ingresos
     *
     * @return
     */
    public ArrayList<Double> getEntryIngresos(String Instancias_ID) {
        //valores a usar.
        double ingreso;

        //lista a retornar

        ArrayList<Double> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getDouble(5);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get the data from column ingresos in the actual month
     *
     * @return List with the ingreso data
     */
    public ArrayList<Double> getEntryCurrentMonthIngresos(String Instancias_ID) {
        // Value
        double ingreso;

        // List to return
        ArrayList<Double> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                        "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getDouble(5);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get ingresos data from table Saldos between two given dates
     *
     * @param beginDate Begin date
     * @param finalDate Final date
     * @return List with the ingreso data
     */
    public ArrayList<Double> getEntryInDateIngresos(String Instancias_ID, String beginDate, String finalDate) {
        // Value
        double ingreso;

        // List to return
        ArrayList<Double> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' " +
                        "AND '" + finalDate + "') AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getDouble(5);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get the data from column ingresos in the given month and year
     *
     * @return
     */
    public ArrayList<Double> getEntryInMonthYearIngresos(String Instancias_ID, String month, String year) {
        //valores a usar.
        double ingreso;

        //lista a retornar

        ArrayList<Double> ingresos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                        "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            ingreso = consulta.getDouble(5);
            ingresos.add(ingreso);
        }

        return ingresos;
    }

    /**
     * Get the data from in the given month and year with specific category
     *
     * @return
     */
    public Cursor getEntryInMonthYearByCategory(String Instancias_ID, int category, String month, String year) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                    "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID, null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                    "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category, null);
        }

        return resultado;
    }

    /**
     * Get the Profit data from in the given month and year with specific category
     *
     * @return
     */
    public Cursor getIngresosInMonthYearByCategory(String Instancias_ID, int category, String month, String year) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                    "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID + " AND Gasto=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                    "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Gasto=0", null);
        }

        return resultado;
    }

    /**
     * Get the Spend data from in the given month and year with specific category
     *
     * @return
     */
    public Cursor getGastosInMonthYearByCategory(String Instancias_ID, int category, String month, String year) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                    "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID + " AND Ingreso=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                    "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Ingreso=0", null);
        }

        return resultado;
    }

    /**
     * Get the data from column Gasto in the current month
     *
     * @return
     */
    public ArrayList<Double> getEntryCurrentMonthGastos(String Instancias_ID) {
        //valores a usar.
        double gasto;

        //lista a retornar

        ArrayList<Double> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                        "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getDouble(6);
            gastos.add(gasto);
        }

        return gastos;
    }

    /**
     * Get gastos data from table Saldos between two given dates
     *
     * @param beginDate Begin date
     * @param finalDate Final date
     * @return List with the gasto data
     */
    public ArrayList<Double> getEntryInDateGastos(String Instancias_ID, String beginDate, String finalDate) {
        // Value
        double gasto;

        // List to return
        ArrayList<Double> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' " +
                        "AND '" + finalDate + "') AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getDouble(6);
            gastos.add(gasto);
        }

        return gastos;
    }

    /**
     * Get the data from column Gasto in the given month and year
     *
     * @return
     */
    public ArrayList<Double> getEntryInMonthYearGastos(String Instancias_ID, String month, String year) {
        //valores a usar.
        double gasto;

        //lista a retornar

        ArrayList<Double> gastos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)='" + year + "' " +
                        "AND strftime('%m',Fecha)='" + month + "' AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            gasto = consulta.getDouble(6);
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
     * Get all the profit data from table Saldos
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryAllProfit(String Instancias_ID, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID + " AND Gasto=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Gasto=0", null);
        }

        return resultado;
    }

    /**
     * Get all the spend data from table Saldos
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryAllSpend(String Instancias_ID, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID + " AND Ingreso=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Ingreso=0", null);
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
     * Get the profit data from table Saldos in the actual month
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryMonthProfit(String Instancias_ID, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                    "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID + " AND Gasto=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                    "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Gasto=0", null);
        }

        return resultado;
    }

    /**
     * Get the spend data from table Saldos in the actual month
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryMonthSpend(String Instancias_ID, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                    "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID + " AND Ingreso=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE strftime('%Y',Fecha)=strftime('%Y',date('now')) " +
                    "AND strftime('%m',Fecha)=strftime('%m',date('now')) AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Ingreso=0", null);
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
     * Get the profit data from table Saldos from beginning to a given date
     *
     * @param finalDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryProfitFromBegToDate(String Instancias_ID, String finalDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha <= '" + finalDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Gasto=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha <= '" + finalDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Gasto=0", null);
        }
        return resultado;
    }

    /**
     * Get the spend data from table Saldos from beginning to a given date
     *
     * @param finalDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntrySpendFromBegToDate(String Instancias_ID, String finalDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha <= '" + finalDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Ingreso=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha <= '" + finalDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Ingreso=0", null);
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
     * Get all the profit data from table Saldos from a given date to the end of times
     *
     * @param beginDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryProfitFromDateToToday(String Instancias_ID, String beginDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha >= '" + beginDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Gasto=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha >= '" + beginDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Gasto=0", null);
        }
        return resultado;
    }

    /**
     * Get all the spend data from table Saldos from a given date to the end of times
     *
     * @param beginDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntrySpendFromDateToToday(String Instancias_ID, String beginDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha >= '" + beginDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Ingreso=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE Fecha >= '" + beginDate + "' " +
                    "AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Ingreso=0", null);
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
     * Get all the profit data from table Saldos between two given dates
     *
     * @param beginDate
     * @param finalDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntryProfitInDate(String Instancias_ID, String beginDate, String finalDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' " +
                    "AND '" + finalDate + "') AND Instancias_ID=" + Instancias_ID + " AND Gasto=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' " +
                    "AND '" + finalDate + "') AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Gasto=0", null);
        }
        return resultado;
    }

    /**
     * Get all the spend data from table Saldos between two given dates
     *
     * @param beginDate
     * @param finalDate
     * @return Cursor with all the information in all the columns
     */
    public Cursor getEntrySpendInDate(String Instancias_ID, String beginDate, String finalDate, int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        if (category == 0) {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' " +
                    "AND '" + finalDate + "') AND Instancias_ID=" + Instancias_ID + " AND Ingreso=0", null);
        } else {
            resultado = db.rawQuery("SELECT * FROM " + TABLA1_NOMBRE + " WHERE (Fecha BETWEEN '" + beginDate + "' " +
                    "AND '" + finalDate + "') AND Instancias_ID=" + Instancias_ID + " AND Categorias_ID=" + category + " AND Ingreso=0", null);
        }
        return resultado;
    }

    /**
     * Get the data from Ahorro of type "A"
     *
     * @return
     */
    public ArrayList<Double> getSaveAllPayment(String Instancias_ID) {
        //valores a usar.
        double abono;

        //lista a retornar

        ArrayList<Double> abonos = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA4_NOMBRE + " WHERE Tipo='A' AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            abono = consulta.getDouble(2);
            abonos.add(abono);
        }

        return abonos;
    }

    /**
     * Get the data from Ahorro of type "R"
     *
     * @return
     */
    public ArrayList<Double> getSaveAllWithdrawal(String Instancias_ID) {
        //valores a usar.
        double retiro;

        //lista a retornar

        ArrayList<Double> retiros = new ArrayList<>();

        Cursor consulta = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLA4_NOMBRE + " WHERE Tipo='R' AND Instancias_ID=" + Instancias_ID, null);

        while (consulta.moveToNext()) {
            retiro = consulta.getDouble(2);
            retiros.add(retiro);
        }

        return retiros;
    }

    /**
     * Get all the dept data from table Deudas
     *
     * @return Cursor with all the information in all the columns
     */
    public Cursor getAllDeptsData(String Instancias_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultado;

        resultado = db.rawQuery("SELECT * FROM " + TABLA5_NOMBRE + " WHERE Instancias_ID=" + Instancias_ID, null);

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
        return db.delete(TABLA2_NOMBRE, "Instancias_ID=?", new String[]{Instancias_ID});
    }

    /**
     * Delete a row from table categorias give by the category_id
     *
     * @param id
     * @return
     */
    public Integer deleteCategory(String id, String instance_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA3_NOMBRE, "Categorias_ID=? AND Instancias_ID=?", new String[]{id, instance_id});
    }

    /**
     * Delete a row from Deudas table, given by the entry id and instance id
     *
     * @param id
     * @param instance_id
     * @return
     */
    public Integer deleteDept(String id, String instance_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA5_NOMBRE, "ID=? AND Instancias_ID=?", new String[]{id, instance_id});
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Update methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
    public boolean editEntryData(String id, String date, String hora, double ingreso, double gasto, String descr, String inst_ID, String new_inst_ID, String categ_ID) {
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

    /**
     * Edit a category with the given category id
     *
     * @param category_ID
     * @return
     */
    public boolean editCategory(String category_ID, String name, String icon, String instance_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Categorias_ID", category_ID);
        contentValues.put(Col_NombreCateg, name);
        contentValues.put(Col_Icono, icon);
        contentValues.put(Col_InstIDCateg, instance_id);

        db.update(TABLA3_NOMBRE, contentValues, "Categorias_ID=? AND Instancias_ID=?", new String[]{category_ID, instance_id});

        return true;
    }

    /**
     * Update dept row from Deudas table
     *
     * @param instance_ID
     * @param new_instance_ID
     * @param dept_ID
     * @param description
     * @param amount
     * @param date
     * @return
     */
    public boolean editDept(String instance_ID, String new_instance_ID, String dept_ID, String description, Double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", dept_ID);
        contentValues.put(Col_InstanciaID, new_instance_ID);
        contentValues.put(Col_Fecha, date);
        contentValues.put(Col_MontoDeuda, amount);
        contentValues.put(Col_Descripcion, description);

        db.update(TABLA5_NOMBRE, contentValues, "ID=? AND Instancias_ID=?", new String[]{dept_ID, instance_ID});

        return true;
    }
}