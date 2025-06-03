package edu.alfonsaco.codezen.ui.ranking.ranking_utils;

public class Usuario {
    private int contLogros;
    private String username;
    private int nivel;

    // CONSTRUCTOR
    public Usuario(int contLogros, String username, int nivel) {
        this.contLogros = contLogros;
        this.username = username;
        this.nivel = nivel;
    }
    public Usuario() {
    }

    // GETTERS Y SETTERS
    public int getContLogros() {return contLogros;}
    public void setContLogros(int contLogros) {this.contLogros = contLogros;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public int getNivel() {return nivel;}
    public void setNivel(int nivel) {this.nivel = nivel;}
}
