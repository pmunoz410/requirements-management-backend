package pm.dev.code.requirements_management_backend.dto.requirements;

import pm.dev.code.requirements_management_backend.enums.Priority;
import pm.dev.code.requirements_management_backend.enums.RequirementClassification;
import pm.dev.code.requirements_management_backend.enums.RequirementStatus;

import java.time.LocalDateTime;

public record RequirementResponse(
        Long id,
        String title,
        String description,
        RequirementStatus status,
        Priority priority,
        RequirementClassification classification,
        Long workflowId,
        Long assigneeId,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
