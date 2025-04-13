package edu.alfonsaco.codezen.ui.habits.habit_utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Habit implements Serializable {
    private String nombre;
    private String descripcion;
    private String color;
    private ArrayList<Day> dias;

    // CONSTRUCTOR
    public Habit(String nombre, String descripcion, String color) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.dias = new ArrayList<>();
    }
    public Habit() {

    }

    //  GETTERS Y SETTERS
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
    public ArrayList<Day> getDias() {return dias;}
    public void setDias(ArrayList<Day> dias) {this.dias = dias;}
}
