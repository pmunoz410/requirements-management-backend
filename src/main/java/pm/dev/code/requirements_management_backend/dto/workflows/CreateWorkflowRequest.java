package pm.dev.code.requirements_management_backend.dto.workflows;

public record CreateWorkflowRequest(
        String name,
        Long areaId
) {
}
