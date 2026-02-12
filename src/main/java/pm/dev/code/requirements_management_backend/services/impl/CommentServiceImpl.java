package pm.dev.code.requirements_management_backend.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.comments.CommentResponse;
import pm.dev.code.requirements_management_backend.dto.comments.CreateCommentRequest;
import pm.dev.code.requirements_management_backend.dto.comments.UpdateCommentRequest;
import pm.dev.code.requirements_management_backend.entities.*;
import pm.dev.code.requirements_management_backend.enums.CommentType;
import pm.dev.code.requirements_management_backend.repositories.ICommentRepository;
import pm.dev.code.requirements_management_backend.repositories.IRequirementRepository;
import pm.dev.code.requirements_management_backend.services.ICommentService;
import pm.dev.code.requirements_management_backend.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final ICommentRepository commentRepository;
    private final IRequirementRepository requirementRepository;
    private final SecurityUtils securityUtils;

    @Override
    public List<CommentResponse> getCommentsByRequirement(Long requirementId) {
        User currentUser = securityUtils.getCurrentUser();

        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new EntityNotFoundException("Requirement not found"));

        List<Comment> comments;

        switch (currentUser.getRol()) {
            case USUARIO -> {
                // Solo puede ver si es el assignee
                if (!requirement.getAssignee().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not assigned to this requirement");
                }
                comments = commentRepository.findByRequirementAndType(requirement, CommentType.PUBLIC);
            }
            case ADMIN -> {
                // Solo puede ver si es admin del workflow del requerimiento
                if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not the administrator of this workflow");
                }
                comments = commentRepository.findByRequirement(requirement);
            }
            case SUPER_ADMIN -> {
                OrganizationalArea area = requirement.getWorkflow().getArea();

                if (!area.getCreatedBy().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not allowed to view comments for this requirement");
                }
                comments = commentRepository.findByRequirement(requirement);
            }
            default -> throw new AccessDeniedException("Unknown role");
        }

//        if (currentUser.getRol() == Role.USUARIO) {
//            comments = commentRepository.findByRequirementAndType(requirement, CommentType.PUBLIC);
//        } else {
//            comments = commentRepository.findByRequirement(requirement);
//        }

        return comments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CommentResponse createComment(Long requirementId, CreateCommentRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new EntityNotFoundException("Requirement not found"));

        Workflow workflow = requirement.getWorkflow();

        switch (currentUser.getRol()) {
            case USUARIO -> {
                // El usuario debe ser el asignado al requerimiento
                if (!requirement.getAssignee().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not assigned to this requirement");
                }

                // Solo puede crear comentarios PUBLIC
                if (request.type() != CommentType.PUBLIC) {
                    throw new AccessDeniedException("USUARIO can only create PUBLIC comments");
                }
            }
            case ADMIN -> {
                // Debe ser el administrador del workflow
                if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not administrator of this workflow");
                }
            }
            case SUPER_ADMIN -> {
                // Debe ser SUPER_ADMIN de la área correspondiente
                if (!workflow.getArea().getCreatedBy().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not SUPER_ADMIN of this area");
                }

                // Solo puede crear comentarios INTERNAL
                if (request.type() != CommentType.INTERNAL) {
                    throw new AccessDeniedException("SUPER_ADMIN can only create INTERNAL comments");
                }
            }
            default -> throw new AccessDeniedException("Role not allowed to create comments");
        }

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setType(request.type());
        comment.setUser(currentUser);
        comment.setRequirement(requirement);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    @Override
    public CommentResponse updateComment(Long requirementId, Long commentId, UpdateCommentRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getRequirement().getId().equals(requirementId)) {
            throw new AccessDeniedException("Comment does not belong to this requirement");
        }

        Workflow workflow = comment.getRequirement().getWorkflow();

        switch (currentUser.getRol()) {
            case USUARIO -> {
                // Solo puede editar sus propios comentarios
                if (!comment.getUser().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You can only edit your own comments");
                }
                // Solo comentarios PUBLIC
                if (comment.getType() != CommentType.PUBLIC) {
                    throw new AccessDeniedException("USUARIO can only edit PUBLIC comments");
                }
                // Solo si es assignee del requerimiento
                if (!comment.getRequirement().getAssignee().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not assigned to this requirement");
                }
            }
            case ADMIN -> {
                if (!comment.getUser().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("ADMIN can only edit their own comments");
                }
                // Debe ser admin del workflow
                if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not administrator of this workflow");
                }
            }
            case SUPER_ADMIN -> {
                if (!comment.getUser().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("SUPER_ADMIN can only edit their own comments");
                }
                if (comment.getType() != CommentType.INTERNAL) {
                    throw new AccessDeniedException("SUPER_ADMIN can only edit INTERNAL comments");
                }
                // Debe ser SUPER_ADMIN de la área correspondiente
                if (!workflow.getArea().getCreatedBy().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You are not SUPER_ADMIN of this area");
                }
            }
            default -> throw new AccessDeniedException("Role not allowed to update comments");
        }

        comment.setContent(request.content());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getType(),
                comment.getUser().getId(),
                comment.getRequirement().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
