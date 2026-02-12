package pm.dev.code.requirements_management_backend.dto.areas;

import java.time.LocalDateTime;
import java.util.List;

public record OrganizationalAreaResponse(
        Long id,
        String name,
        Boolean active,
        List<Long> adminIds,
        Long createdById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
