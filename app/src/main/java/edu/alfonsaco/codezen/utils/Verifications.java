package edu.alfonsaco.codezen.utils;

public class Verifications {

    public boolean esEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
