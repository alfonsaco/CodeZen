package edu.alfonsaco.codezen.ui.habits;

import java.io.Serializable;

public class Habit implements Serializable {
    private String nombre;
    private String descripcion;
    private String color;

    // Constructor
    public Habit(String nombre, String descripcion, String color) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
    }
    public Habit() {

    }

    //  Getters y Setters
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
}
