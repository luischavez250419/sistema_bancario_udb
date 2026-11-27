package com.udb.web.seguridad;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IntentosLogin {

    private static final int MAX_INTENTOS = 5;
    private static final long TIEMPO_BLOQUEO = 5 * 60 * 1000;

    private static final ConcurrentMap<String, RegistroIntentos> intentos =
            new ConcurrentHashMap<>();

    private static class RegistroIntentos {

        private int cantidad;
        private long bloqueadoHasta;
    }

    // Comprobar si el usuario está temporalmente bloqueado.
    public static boolean estaBloqueado(String clave) {

        RegistroIntentos registro = intentos.get(clave);

        if (registro == null) {
            return false;
        }

        synchronized (registro) {

            if (registro.bloqueadoHasta > System.currentTimeMillis()) {
                return true;
            }

            if (registro.bloqueadoHasta != 0) {
                intentos.remove(clave, registro);
                return false;
            }

            return false;
        }
    }

    // Registrar un intento fallido.
    public static boolean registrarIntentoFallido(String clave) {

        RegistroIntentos registro = intentos.computeIfAbsent(
                clave, k -> new RegistroIntentos()
        );

        synchronized (registro) {

            long ahora = System.currentTimeMillis();

            if (registro.bloqueadoHasta > ahora) {
                return true;
            }

            if (registro.bloqueadoHasta != 0) {
                registro.cantidad = 0;
                registro.bloqueadoHasta = 0;
            }

            registro.cantidad++;

            if (registro.cantidad >= MAX_INTENTOS) {
                registro.bloqueadoHasta = ahora + TIEMPO_BLOQUEO;
                return true;
            }

            return false;
        }
    }

    // Reiniciar el contador después de una autenticación exitosa.
    public static void reiniciarIntentos(String clave) {
        intentos.remove(clave);
    }
}

