package model.utils;

import java.util.regex.Pattern;

/**
 * Classe di utilità per la validazione dei dati di input
 */
public class ValidationUtils {
    
    // Pattern per la validazione email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Pattern per nomi e cognomi (solo lettere, spazi, apostrofi e trattini)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Zàáâäãåąčćđßèéêëėęğğìíîïłńñòóôöõøřšşûüùúűýÿ\\s''-]{2,50}$"
    );
    
    /**
     * Valida un indirizzo email
     * @param email l'email da validare
     * @return true se l'email è valida, false altrimenti
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Valida un nome o cognome
     * @param name il nome da validare
     * @return true se il nome è valido, false altrimenti
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Valida una password
     * @param password la password da validare
     * @return true se la password è valida, false altrimenti
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 6 && password.length() <= 100;
    }
    
    /**
     * Valida un nome azienda
     * @param nomeAzienda il nome dell'azienda da validare
     * @return true se il nome è valido, false altrimenti
     */
    public static boolean isValidCompanyName(String nomeAzienda) {
        if (nomeAzienda == null || nomeAzienda.trim().isEmpty()) {
            return false;
        }
        String trimmed = nomeAzienda.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }
    
    /**
     * Valida un tipo di azienda
     * @param tipo il tipo da validare
     * @return true se il tipo è valido, false altrimenti
     */
    public static boolean isValidCompanyType(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return false;
        }
        String[] validTypes = {"urbana", "extraurbana", "turistica", "ferroviaria"};
        for (String validType : validTypes) {
            if (validType.equals(tipo.trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Pulisce e normalizza una stringa
     * @param input la stringa da pulire
     * @return la stringa pulita
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }
    
    /**
     * Normalizza un'email (trim + lowercase)
     * @param email l'email da normalizzare
     * @return l'email normalizzata
     */
    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}
