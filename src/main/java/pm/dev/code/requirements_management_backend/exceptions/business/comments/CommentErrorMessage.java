package pm.dev.code.requirements_management_backend.exceptions.business.comments;

public enum CommentErrorMessage {

    // ===== NOT FOUND =====
    COMMENT_NOT_FOUND("El comentario no fue encontrado"),

    // ===== OPERATION NOT ALLOWED =====
    VIEW_COMMENTS_NOT_ALLOWED("No tienes permisos para ver los comentarios de este requerimiento"),
    COMMENT_NOT_BELONG_TO_REQUIREMENT("El comentario no pertenece al requerimiento"),
    ONLY_UPDATE_OWN_COMMENTS("Solo puedes modificar tus propios comentarios"),
    NOT_AREA_SUPER_ADMIN("No eres SUPER_ADMIN del área de este requerimiento"),
    ROLE_NOT_ALLOWED("Rol no autorizado para esta operación"),
    ROLE_NOT_ALLOWED_TO_CREATE("No tienes permiso para crear comentarios"),
    ROLE_NOT_ALLOWED_TO_UPDATE("No tienes permiso para actualizar comentarios"),

    // ===== VALIDATION =====
    USER_ONLY_CREATE_PUBLIC_COMMENTS("El usuario solo puede crear comentarios públicos"),
    SUPER_ADMIN_ONLY_CREATE_INTERNAL_COMMENTS("El SUPER_ADMIN solo puede crear comentarios internos"),
    USER_ONLY_UPDATE_PUBLIC_COMMENTS("El usuario solo puede actualizar comentarios públicos"),
    SUPER_ADMIN_ONLY_UPDATE_INTERNAL_COMMENTS("El SUPER_ADMIN solo puede modificar comentarios internos");

    private final String message;

    CommentErrorMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
