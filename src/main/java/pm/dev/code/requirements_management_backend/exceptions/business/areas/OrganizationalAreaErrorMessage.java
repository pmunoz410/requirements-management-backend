package pm.dev.code.requirements_management_backend.exceptions.business.areas;

public enum OrganizationalAreaErrorMessage {

    // ===== NOT FOUND =====
    AREA_NOT_FOUND("El área organizacional no existe"),
    ADMIN_USER_NOT_FOUND("El usuario administrador no existe"),

    // ===== ACCESS / PERMISSION =====
    LIST_NOT_ALLOWED("No tienes permisos para listar áreas organizacionales"),
    VIEW_NOT_ALLOWED("No tienes permisos para ver esta área organizacional"),
    CREATE_NOT_ALLOWED("Solo SUPER_ADMIN puede crear áreas organizacionales"),
    UPDATE_NOT_ALLOWED("Solo SUPER_ADMIN puede actualizar áreas organizacionales"),
    DELETE_NOT_ALLOWED("Solo SUPER_ADMIN puede eliminar áreas organizacionales"),
    ASSIGN_ADMIN_NOT_ALLOWED("Solo SUPER_ADMIN puede asignar administradores"),
    REMOVE_ADMIN_NOT_ALLOWED("Solo SUPER_ADMIN puede remover administradores"),
    CHANGE_ADMIN_AREA_NOT_ALLOWED("Solo SUPER_ADMIN puede cambiar administradores de área"),
    ACCESS_DENIED("No cuentas con los permisos necesarios"),

    // ===== BUSINESS CONFLICT =====
    ADMIN_ALREADY_ASSIGNED("El administrador ya está asignado a esta área"),
    ADMIN_NOT_ASSIGNED("El administrador no está asociado a esta área"),
    ADMIN_NOT_IN_SOURCE_AREA("El administrador no pertenece al área origen"),

    // ===== VALIDATION =====
    USER_NOT_ADMIN_ROLE("El usuario no tiene rol ADMIN");

    private final String message;

    OrganizationalAreaErrorMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
