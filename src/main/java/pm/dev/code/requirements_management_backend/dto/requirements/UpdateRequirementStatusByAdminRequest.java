package pm.dev.code.requirements_management_backend.dto.requirements;

import pm.dev.code.requirements_management_backend.enums.RequirementStatus;

public record UpdateRequirementStatusByAdminRequest(
        RequirementStatus status,
        String comment
) {
}
