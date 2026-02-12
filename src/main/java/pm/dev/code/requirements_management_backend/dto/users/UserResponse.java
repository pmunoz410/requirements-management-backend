package pm.dev.code.requirements_management_backend.dto.users;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String role,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
