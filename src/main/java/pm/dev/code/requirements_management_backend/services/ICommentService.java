package pm.dev.code.requirements_management_backend.services;

import pm.dev.code.requirements_management_backend.dto.comments.CommentResponse;
import pm.dev.code.requirements_management_backend.dto.comments.CreateCommentRequest;
import pm.dev.code.requirements_management_backend.dto.comments.UpdateCommentRequest;

import java.util.List;

public interface ICommentService {
    List<CommentResponse> getCommentsByRequirement(Long requirementId);
    CommentResponse createComment(Long requirementId, CreateCommentRequest request);
    CommentResponse updateComment(Long requirementId, Long commentId, UpdateCommentRequest request);
}
