package pm.dev.code.requirements_management_backend.exceptions.business.workflows;

public enum WorkflowErrorMessage {

    // ===== NOT FOUND =====
    WORKFLOW_NOT_FOUND("El workflow no fue encontrado"),

    // ===== OPERATION NOT ALLOWED =====
    LIST_NOT_ALLOWED("No tienes permisos para listar workflows"),
    VIEW_NOT_ALLOWED("No tienes permisos para ver este workflow"),
    CREATE_NOT_ALLOWED("Solo ADMIN puede crear workflows"),
    UPDATE_NOT_ALLOWED("Solo ADMIN puede actualizar workflows"),
    DELETE_NOT_ALLOWED("Solo ADMIN puede eliminar workflows"),
    ASSIGN_USERS_NOT_ALLOWED("Solo ADMIN puede asignar usuarios"),
    REMOVE_USERS_NOT_ALLOWED("Solo ADMIN puede remover usuarios"),
    NOT_WORKFLOW_OWNER("No eres el propietario administrador de este workflow"),
    CHANGE_AREA_NOT_ALLOWED("No se puede cambiar el área del workflow"),

    // ===== BUSINESS CONFLICT =====
    USER_ALREADY_ASSIGNED("El usuario ya está asignado al workflow"),
    USER_NOT_ASSIGNED("El usuario no está asignado al workflow"),

    // ===== VALIDATION =====
    USER_NOT_USER_ROLE("Solo usuarios con rol USUARIO pueden ser asignados");

    private final String message;

    WorkflowErrorMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
