package pm.dev.code.requirements_management_backend.dto.requirements;

import pm.dev.code.requirements_management_backend.enums.Priority;
import pm.dev.code.requirements_management_backend.enums.RequirementClassification;

public record UpdateRequirementDetailsRequest(
        String title,
        String description,
        Priority priority,
        RequirementClassification classification
) {
}
