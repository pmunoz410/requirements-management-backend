package pm.dev.code.requirements_management_backend.exceptions.security;

public enum JwtErrorMessage {

    INVALID_TOKEN("El token es inválido o mal formado"),
    EXPIRED_TOKEN("El token ha expirado"),
    MISSING_TOKEN("No se proporcionó token");

    private final String message;

    JwtErrorMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
