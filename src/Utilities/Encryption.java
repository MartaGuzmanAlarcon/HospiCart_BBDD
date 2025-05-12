package Utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Encryption {

	    public static String encryptPasswordMD5(String password) {
	        try {
	            // Crear instancia de MessageDigest para MD5
	            MessageDigest md = MessageDigest.getInstance("MD5");

	            // Convertir la contraseña en bytes y calcular el hash
	            byte[] hashBytes = md.digest(password.getBytes());

	            // Convertir los bytes a formato hexadecimal
	            StringBuilder hexString = new StringBuilder();
	            for (byte b : hashBytes) {
	                String hex = Integer.toHexString(0xff & b);
	                if (hex.length() == 1) hexString.append('0');
	                hexString.append(hex);
	            }
	            return hexString.toString();
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException("Error al encriptar la contraseña: " + e.getMessage());
	        }
	    }
	    
	    
	}


