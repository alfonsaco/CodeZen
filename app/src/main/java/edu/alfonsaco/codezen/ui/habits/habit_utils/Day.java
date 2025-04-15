package edu.alfonsaco.codezen.ui.habits.habit_utils;

public class Day {
    private boolean completado;
    private String fecha;

    // CONSTRUCTORES
    public Day(boolean completado, String fecha) {
        this.completado = completado;
        this.fecha = fecha;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
