package pm.dev.code.requirements_management_backend.dto.comments;

import pm.dev.code.requirements_management_backend.enums.CommentType;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        CommentType type,
        Long userId,
        Long requirementId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
