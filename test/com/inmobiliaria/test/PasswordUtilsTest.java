package com.inmobiliaria.test;

import com.inmobiliaria.util.PasswordUtils;

public class PasswordUtilsTest {

    public static void ejecutar() {
        String salt = PasswordUtils.generarSalt();
        String salto = PasswordUtils.generarSalt();
        if (salt == null || salt.isEmpty()) {
            throw new AssertionError("generarSalt debe devolver un valor no vacio");
        }
        if (!(salt.length() == 24 && salto.length() == 24)) {
            throw new AssertionError("Un salt de 16 bytes debe codificarse en Base64 de 24 caracteres");
        }
        if (salt.equals(salto)) {
            throw new AssertionError("generarSalt debe producir valores distintos entre llamadas");
        }

        String h1 = PasswordUtils.hashPassword("cliente123", salt);
        String h2 = PasswordUtils.hashPassword("cliente123", salt);
        if (!h1.equals(h2)) {
            throw new AssertionError("El hash debe ser determinista para la misma contrasena y salt");
        }
        if (!h1.equals(PasswordUtils.hashPassword("cliente123", salt))) {
            throw new AssertionError("Hash no reproducible");
        }

        String h3 = PasswordUtils.hashPassword("otraClave", salt);
        if (h1.equals(h3)) {
            throw new AssertionError("Contrasenas distintas no deben producir el mismo hash");
        }

        String saltSeed = "58hxMHRL8BO9IMLrhdDQSw==";
        String hashEsperado = "1NOCcuWaOMbPLO4F+22v45tvpEU6LoV6EFymNQF8ep0=";
        String hashCalculado = PasswordUtils.hashPassword("cliente123", saltSeed);
        if (!hashEsperado.equals(hashCalculado)) {
            throw new AssertionError("El vector de prueba de los datos sembrados no coincide: " + hashCalculado);
        }

        System.out.println("[OK] PasswordUtilsTest: sal, determinismo y vector de datos sembrados");
    }
}