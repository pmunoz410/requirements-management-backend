package pm.dev.code.requirements_management_backend.exceptions.technical;

public enum TechnicalErrorMessage {

    INTERNAL_ERROR("Error interno del sistema"),
    DATABASE_ERROR("Error de base de datos"),
    JWT_GENERATION_ERROR("Error al generar o validar el token"),
    EXTERNAL_SERVICE_ERROR("Error en servicio externo"),
    FILE_IO_ERROR("Error al procesar archivos o IO"),
    INTEGRITY_ERROR("Error de integridad de datos");

    private final String message;

    TechnicalErrorMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
