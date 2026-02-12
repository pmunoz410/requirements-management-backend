package pm.dev.code.requirements_management_backend.dto.workflows;

import java.time.LocalDateTime;

public record WorkflowResponse(
        Long id,
        String name,
        Long areaId,
        Long adminId,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
