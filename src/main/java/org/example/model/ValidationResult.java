package org.example.model;

/**
 * Classe para encapsular o resultado da validação dos dados de entrada.
 */
public class ValidationResult {
    private final boolean valid;
    private final String message;
    private final Double P0;
    private final Double r;
    private final Double T;
    private final Double deltaT;

    public ValidationResult(boolean valid, String message) {
        this(valid, message, null, null, null, null);
    }

    public ValidationResult(boolean valid, String message, Double P0, Double r, Double T, Double deltaT) {
        this.valid = valid;
        this.message = message;
        this.P0 = P0;
        this.r = r;
        this.T = T;
        this.deltaT = deltaT;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public Double getP0() {
        return P0;
    }

    public Double getR() {
        return r;
    }

    public Double getT() {
        return T;
    }

    public Double getDeltaT() {
        return deltaT;
    }
}
