package edu.alfonsaco.codezen.ui.meditation.meditate;

public class SpinnerItem {
    private String texto;
    private int icono;
    // Ej: 0 sin música, 1 ruido blanco...
    private int valorAudio;

    // CONSTRUCTORES
    public SpinnerItem(String texto, int icono, int valorAudio) {
        this.texto = texto;
        this.icono = icono;
        this.valorAudio = valorAudio;
    }

    // GETTERS Y SETTERS
    public int getIcono() {
        return icono;
    }
    public String getTexto() { return texto;}
    public int getValorAudio() { return valorAudio;}
}
