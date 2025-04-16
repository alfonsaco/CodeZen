package edu.alfonsaco.codezen.ui.habits.habit_utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Habit implements Serializable {
    private String id;
    private String nombre;
    private String descripcion;
    private String color;
    private String recordatorio;
    private List<Day> dias = new ArrayList<>();

    // CONSTRUCTOR
    public Habit(String id, String nombre, String descripcion, String color, String recordatorio, ArrayList<Day> dias) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.recordatorio = recordatorio;
        this.dias = dias;
    }
    public Habit() {

    }

    //  GETTERS Y SETTERS
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getNombre() {
        return nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public List<Day> getDias() {
        return dias != null ? dias : new ArrayList<>();
    }

    public void setDias(List<Day> dias) {
        this.dias = dias != null ? dias : new ArrayList<>();
    }
    public String getRecordatorio() {return recordatorio;}
    public void setRecordatorio(String recordatorio) {this.recordatorio = recordatorio;}
}
