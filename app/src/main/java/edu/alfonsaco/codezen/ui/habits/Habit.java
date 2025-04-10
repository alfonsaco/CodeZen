package edu.alfonsaco.codezen.ui.habits;

import java.io.Serializable;

public class Habit implements Serializable {
    private String nombre;
    private String descripcion;

    // Constructor
    public Habit(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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
}
