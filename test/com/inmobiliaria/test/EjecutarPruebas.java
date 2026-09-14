package com.inmobiliaria.test;

public class EjecutarPruebas {

    public static void main(String[] args) {
        boolean ok = true;
        try {
            PasswordUtilsTest.ejecutar();
        } catch (AssertionError e) {
            ok = false;
            System.out.println("[FALLO] PasswordUtilsTest: " + e.getMessage());
        }
        try {
            UsuarioTest.ejecutar();
        } catch (AssertionError e) {
            ok = false;
            System.out.println("[FALLO] UsuarioTest: " + e.getMessage());
        }
        if (ok) {
            System.out.println("Todas las pruebas unitarias pasaron correctamente.");
        } else {
            System.out.println("Existen fallos en las pruebas unitarias.");
            System.exit(1);
        }
    }
}