package pm.dev.code.requirements_management_backend.dto.areas;

import java.util.List;

public record AssignAdminsRequest(
        List<Long> adminIds
) {
}
