package com.example.multilab.DTO;

import java.time.LocalDateTime;

/**
 * ═══════════════════════════════════════════════════════════
 * ApiError — DTO pour les réponses d'erreur structurées
 *
 * Remplace les RuntimeException("not found") non gérées
 * par des réponses JSON propres avec code, message et timestamp
 * ═══════════════════════════════════════════════════════════
 */
public class ApiError {

    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ApiError() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // ─── Getters & Setters ───
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
