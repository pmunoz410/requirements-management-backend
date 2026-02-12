package pm.dev.code.requirements_management_backend.dto.comments;

import pm.dev.code.requirements_management_backend.enums.CommentType;

public record CreateCommentRequest(
        String content,
        CommentType type
) {
}
