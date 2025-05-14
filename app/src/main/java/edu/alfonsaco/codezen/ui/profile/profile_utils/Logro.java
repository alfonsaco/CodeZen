package edu.alfonsaco.codezen.ui.profile.profile_utils;

public class Logro {
    private String id;
    private String nombre;
    private String descripcion;
    private String ruta;

    // CONSTRUCTOR
    public Logro(String id, String nombre, String descripcion, String ruta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ruta = ruta;
    }

    // GETTERS Y SETTERS
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    public String getRuta() {return ruta;}
    public void setRuta(String ruta) {this.ruta = ruta;}
}
