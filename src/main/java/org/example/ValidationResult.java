package org.example;

public class ValidationResult {
    private final boolean valid;
    private final String message;
    private final double P0;
    private final double r;
    private final double T;
    private final double deltaT;

    public ValidationResult(boolean valid, String message) {
        this(valid, message, 0, 0, 0, 0);
    }

    public ValidationResult(boolean valid, String message, double P0, double r, double T, double deltaT) {
        this.valid = valid;
        this.message = message;
        this.P0 = P0;
        this.r = r;
        this.T = T;
        this.deltaT = deltaT;
    }

    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
    public double getP0() { return P0; }
    public double getR() { return r; }
    public double getT() { return T; }
    public double getDeltaT() { return deltaT; }
}
