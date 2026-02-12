package pm.dev.code.requirements_management_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pm.dev.code.requirements_management_backend.dto.comments.CommentResponse;
import pm.dev.code.requirements_management_backend.dto.comments.CreateCommentRequest;
import pm.dev.code.requirements_management_backend.dto.comments.UpdateCommentRequest;
import pm.dev.code.requirements_management_backend.services.ICommentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requirements/{requirementId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @GetMapping
    public List<CommentResponse> getComments(@PathVariable Long requirementId) {
        return commentService.getCommentsByRequirement(requirementId);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long requirementId, @RequestBody CreateCommentRequest request) {
        CommentResponse created = commentService.createComment(requirementId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @PutMapping("/{commentId}")
    public CommentResponse updateComment(@PathVariable Long requirementId, @PathVariable Long commentId, @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(requirementId, commentId, request);
    }
}
