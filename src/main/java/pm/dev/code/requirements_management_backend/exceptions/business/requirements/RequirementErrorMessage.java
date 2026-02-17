package pm.dev.code.requirements_management_backend.exceptions.business.requirements;

public enum RequirementErrorMessage {

    // ===== NOT FOUND =====
    REQUIREMENT_NOT_FOUND("El requerimiento no fue encontrado"),

    // ===== OPERATION NOT ALLOWED =====
    LIST_NOT_ALLOWED("No tienes permisos para listar requerimientos"),
    VIEW_NOT_ALLOWED("No tienes permisos para ver este requerimiento"),
    VIEW_WORKFLOW_REQUIREMENTS_NOT_ALLOWED("No tienes permisos para ver los requerimientos de este workflow"),
    CREATE_NOT_ALLOWED("Solo ADMIN puede crear requerimientos"),
    UPDATE_NOT_ALLOWED("Solo ADMIN puede actualizar requerimientos"),
    DELETE_NOT_ALLOWED("Solo ADMIN puede eliminar requerimientos"),
    ASSIGN_NOT_ALLOWED("Solo ADMIN puede asignar requerimientos"),
    UNASSIGN_NOT_ALLOWED("Solo ADMIN puede desasignar requerimientos"),
    NOT_ASSIGNED_TO_REQUIREMENT("No estás asignado a este requerimiento"),
    USER_NOT_IN_WORKFLOW("El usuario no pertenece a este workflow"),
    INVALID_STATUS_TRANSITION("Transición de estado no permitida"),

    // ===== BUSINESS CONFLICT =====
    REQUIREMENT_ALREADY_ENABLED("El requerimiento ya está habilitado"),
    REQUIREMENT_ALREADY_DISABLED("El requerimiento ya está deshabilitado"),
    ENABLE_NOT_ALLOWED("Solo ADMIN puede habilitar requerimientos"),
    DISABLE_NOT_ALLOWED("Solo ADMIN puede deshabilitar requerimientos"),
    USER_ALREADY_ASSIGNED("Este usuario ya está asignado al requerimiento"),
    REQUIREMENT_ALREADY_ASSIGNED("El requerimiento ya tiene un usuario asignado"),
    REQUIREMENT_NOT_ASSIGNED("El requerimiento no tiene un usuario asignado"),

    // ===== VALIDATION =====
    USER_NOT_USER_ROLE("Solo usuarios con rol USUARIO pueden ser asignados"),
    COMMENT_REQUIRED("El comentario es obligatorio para este estado"),
    FINALIZED_REQUIREMENT("Los requerimientos finalizados no pueden modificarse");

    private final String message;

    RequirementErrorMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
