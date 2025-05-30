package edu.alfonsaco.codezen.ui.profile.profile_utils;

public class Logro {
    private String id;
    private String nombre;
    private String descripcion;
    private String ruta;
    private boolean desbloqueado;

    // CONSTRUCTOR
    public Logro(String id, String nombre, String descripcion, String ruta, boolean desbloqueado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ruta = ruta;
        this.desbloqueado = desbloqueado;
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
    public void setDesbloqueado(boolean desbloqueado) {this.desbloqueado = desbloqueado;}
    public boolean isDesbloqueado() {return desbloqueado;}
}
