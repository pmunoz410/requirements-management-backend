package pm.dev.code.requirements_management_backend.dto.workflows;

import java.util.List;

public record AssignUsersToWorkflowRequest(
        List<Long> userIds
) {
}
