package edu.alfonsaco.codezen.ui.habits.habit_utils;

import java.io.Serializable;

public class Day implements Serializable {
    private boolean completado;
    // EL ID SERÁ LA FECHA DEL DÍA
    private String id;
    private String color;

    // CONSTRUCTORES
    public Day(boolean completado, String fecha, String colorCompletado) {
        this.completado = completado;
        this.id = fecha;
        this.color = colorCompletado;
    }
    public Day() {

    }

    // GETTERS Y SETTERS
    public boolean isCompletado() {
        return completado;
    }
    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getColor() {return color;}
    public void setColor(String color) {this.color = color;}
}
