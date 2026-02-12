package pm.dev.code.requirements_management_backend.dto.workflows;

public record UpdateWorkflowRequest(
        String name,
        Boolean active,
        Long areaId
) {
}
