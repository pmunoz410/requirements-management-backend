package pm.dev.code.requirements_management_backend.exceptions.business.auth;

public enum AuthErrorMessage {

    INVALID_CREDENTIALS("Usuario o contraseña incorrectos"),
    UNAUTHORIZED_ACCESS("No tienes permisos para acceder a este recurso"),
    REFRESH_NOT_ALLOWED("No tienes permisos para refrescar el token");

    private final String message;

    AuthErrorMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
