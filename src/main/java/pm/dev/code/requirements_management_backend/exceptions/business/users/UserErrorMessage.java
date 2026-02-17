package pm.dev.code.requirements_management_backend.exceptions.business.users;

public enum UserErrorMessage {

    // ===== NOT FOUND =====
    USER_NOT_FOUND("El usuario no fue encontrado"),

    // ===== ACCESS / PERMISSION =====
    ACCESS_DENIED("No tienes permisos para realizar esta acción"),
    LIST_NOT_ALLOWED("No tienes permisos para listar usuarios"),
    VIEW_NOT_ALLOWED("No tienes permisos para ver este usuario"),
    CREATE_NOT_ALLOWED("No tienes permisos para crear este tipo de usuario"),
    UPDATE_NOT_ALLOWED("No tienes permisos para actualizar este tipo de usuario"),
    DELETE_NOT_ALLOWED("No tienes permisos para eliminar este tipo de usuario"),
    ENABLE_NOT_ALLOWED("No tienes permisos para habilitar este tipo de usuario"),
    DISABLE_NOT_ALLOWED("No tienes permisos para deshabilitar este tipo de usuario"),
    RESET_PASSWORD_NOT_ALLOWED("No tienes permisos para restablecer la contraseña de este tipo de usuario"),

    // ===== BUSINESS CONFLICT =====
    USER_ALREADY_ENABLED("El usuario ya está habilitado"),
    USER_ALREADY_DISABLED("El usuario ya está deshabilitado"),
    USERNAME_ALREADY_EXISTS("El nombre de usuario ya existe"),

    // ===== VALIDATION =====
    CURRENT_PASSWORD_INCORRECT("La contraseña actual es incorrecta"),
    INVALID_PASSWORD("La contraseña no cumple con los requisitos");

    private final String message;

    UserErrorMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
