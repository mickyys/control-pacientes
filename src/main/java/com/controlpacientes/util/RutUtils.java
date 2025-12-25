package com.controlpacientes.util;

public class RutUtils {
    
    public static boolean validateRut(String rut) {
        if (rut == null || rut.isEmpty()) return false;
        
        rut = rut.replace(".", "").replace("-", "").toUpperCase();
        if (rut.length() < 8) return false;
        
        try {
            String dv = rut.substring(rut.length() - 1);
            int rutBody = Integer.parseInt(rut.substring(0, rut.length() - 1));
            
            return dv.equals(calculateDV(rutBody));
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static String calculateDV(int rut) {
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return (s != 0) ? String.valueOf(s - 1) : "K";
    }

    public static String formatRut(String rut) {
        if (rut == null || rut.isEmpty()) return "";
        rut = rut.replace(".", "").replace("-", "").toUpperCase();
        if (rut.length() < 2) return rut;
        
        String dv = rut.substring(rut.length() - 1);
        String body = rut.substring(0, rut.length() - 1);
        
        // Add thousands separators
        StringBuilder formatted = new StringBuilder();
        int count = 0;
        for (int i = body.length() - 1; i >= 0; i--) {
            formatted.insert(0, body.charAt(i));
            count++;
            if (count == 3 && i != 0) {
                formatted.insert(0, ".");
                count = 0;
            }
        }
        
        return formatted.append("-").append(dv).toString();
    }
}
