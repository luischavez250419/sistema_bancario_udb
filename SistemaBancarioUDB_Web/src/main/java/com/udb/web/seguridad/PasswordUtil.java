package com.udb.web.seguridad;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERACIONES = 600000;
    private static final int LONGITUD_HASH = 256;
    private static final int LONGITUD_SALT = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    // Generar un salt aleatorio.
    public static String generarSalt() {

        byte[] salt = new byte[LONGITUD_SALT];

        RANDOM.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

    // Generar el hash de una contraseña.
    public static String generarHash(String password, String salt)
            throws NoSuchAlgorithmException {

        byte[] saltBytes = Base64.getDecoder().decode(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                ITERACIONES,
                LONGITUD_HASH
        );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "No se pudo generar el hash de la contraseña.", e
            );

        } finally {

            spec.clearPassword();
        }
    }

    // Verificar una contraseña.
    public static boolean verificarPassword(
            String password,
            String salt,
            String hashAlmacenado) throws NoSuchAlgorithmException {

        String hashGenerado = generarHash(password, salt);

        byte[] hash1 = Base64.getDecoder().decode(hashGenerado);
        byte[] hash2 = Base64.getDecoder().decode(hashAlmacenado);

        return MessageDigest.isEqual(hash1, hash2);
    }
}
