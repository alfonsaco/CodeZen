package edu.alfonsaco.codezen.ui.meditation.meditate;

public class Meditation {
    private String id;
    private String fecha;
    private String duracion;

    // CONSTRUCTOR
    public Meditation(String id, String fecha, String duracion) {
        this.id = id;
        this.fecha = fecha;
        this.duracion = duracion;
    }

    // GETTERS Y SETTERS
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getFecha() {return fecha;}
    public void setFecha(String fecha) {this.fecha = fecha;}
    public String getDuracion() {return duracion;}
    public void setDuracion(String duracion) {this.duracion = duracion;}
}
