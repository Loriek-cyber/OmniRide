package model.udata;
import model.udata.Utente;
import model.util.SessionUtil;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

public class Sessione {
    private String sessionId;
    private Utente utente;
    private long creationTime;
    private long lastAccessTime;
    private boolean isValid;

    public Sessione() {
        this.sessionId = SessionUtil.generateSecureSessionId();
        this.creationTime = Instant.now().getEpochSecond();
        this.lastAccessTime = this.creationTime;
        this.isValid = false; // Inizialmente non valida, sarà validata dopo l'assegnazione dell'utente
    }

    public Sessione(String sessionId, Utente utente, String ipAddress) {
        this.sessionId = sessionId;
        this.utente = utente;
        this.creationTime = Instant.now().getEpochSecond();
        this.lastAccessTime = this.creationTime;
        this.isValid = true;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
