package pm.dev.code.requirements_management_backend.dto.areas;

public record UpdateOrganizationalAreaRequest(
        String name,
        Boolean active
) {
}
