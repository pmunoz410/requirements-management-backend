package pm.dev.code.requirements_management_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.comments.CommentResponse;
import pm.dev.code.requirements_management_backend.dto.comments.CreateCommentRequest;
import pm.dev.code.requirements_management_backend.dto.comments.UpdateCommentRequest;
import pm.dev.code.requirements_management_backend.entities.*;
import pm.dev.code.requirements_management_backend.enums.CommentType;
import pm.dev.code.requirements_management_backend.exceptions.business.comments.CommentErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.business.comments.CommentNotFoundException;
import pm.dev.code.requirements_management_backend.exceptions.business.comments.CommentOperationNotAllowedException;
import pm.dev.code.requirements_management_backend.exceptions.business.requirements.RequirementErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.business.requirements.RequirementNotFoundException;
import pm.dev.code.requirements_management_backend.exceptions.business.requirements.RequirementOperationNotAllowedException;
import pm.dev.code.requirements_management_backend.exceptions.business.workflows.WorkflowErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.business.workflows.WorkflowOperationNotAllowedException;
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
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        List<Comment> comments;

        switch (currentUser.getRol()) {
            case USUARIO -> {
                // Solo puede ver si es el assignee
                if (!requirement.getAssignee().getId().equals(currentUser.getId())) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.VIEW_COMMENTS_NOT_ALLOWED);
                }
                comments = commentRepository.findByRequirementAndType(requirement, CommentType.PUBLIC);
            }
            case ADMIN -> {
                // Solo puede ver si es admin del workflow del requerimiento
                if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.VIEW_COMMENTS_NOT_ALLOWED);
                }
                comments = commentRepository.findByRequirement(requirement);
            }
            case SUPER_ADMIN -> {
                OrganizationalArea area = requirement.getWorkflow().getArea();

                if (!area.getCreatedBy().getId().equals(currentUser.getId())) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.NOT_AREA_SUPER_ADMIN);
                }
                comments = commentRepository.findByRequirement(requirement);
            }
            default -> throw new CommentOperationNotAllowedException(CommentErrorMessage.ROLE_NOT_ALLOWED);
        }

        return comments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CommentResponse createComment(Long requirementId, CreateCommentRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        Workflow workflow = requirement.getWorkflow();

        switch (currentUser.getRol()) {
            case USUARIO -> {
                // El usuario debe ser el asignado al requerimiento
                if (!requirement.getAssignee().getId().equals(currentUser.getId())) {
                    throw new RequirementNotFoundException(RequirementErrorMessage.NOT_ASSIGNED_TO_REQUIREMENT);
                }

                // Solo puede crear comentarios PUBLIC
                if (request.type() != CommentType.PUBLIC) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.USER_ONLY_CREATE_PUBLIC_COMMENTS);
                }
            }
            case ADMIN -> {
                // Debe ser el administrador del workflow
                if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
                    throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
                }
            }
            case SUPER_ADMIN -> {
                // Debe ser SUPER_ADMIN de la área correspondiente
                if (!workflow.getArea().getCreatedBy().getId().equals(currentUser.getId())) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.NOT_AREA_SUPER_ADMIN);
                }

                // Solo puede crear comentarios INTERNAL
                if (request.type() != CommentType.INTERNAL) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.SUPER_ADMIN_ONLY_CREATE_INTERNAL_COMMENTS);
                }
            }
            default -> throw new CommentOperationNotAllowedException(CommentErrorMessage.ROLE_NOT_ALLOWED_TO_CREATE);
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
                .orElseThrow(() -> new CommentNotFoundException(CommentErrorMessage.COMMENT_NOT_FOUND));

        if (!comment.getRequirement().getId().equals(requirementId)) {
            throw new CommentOperationNotAllowedException(CommentErrorMessage.COMMENT_NOT_BELONG_TO_REQUIREMENT);
        }

        Workflow workflow = comment.getRequirement().getWorkflow();

        switch (currentUser.getRol()) {
            case USUARIO -> {
                // Solo puede editar sus propios comentarios
                if (!comment.getUser().getId().equals(currentUser.getId())) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.ONLY_UPDATE_OWN_COMMENTS);
                }
                // Solo comentarios PUBLIC
                if (comment.getType() != CommentType.PUBLIC) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.USER_ONLY_UPDATE_PUBLIC_COMMENTS);
                }
                // Solo si es assignee del requerimiento
                if (!comment.getRequirement().getAssignee().getId().equals(currentUser.getId())) {
                    throw new RequirementOperationNotAllowedException(RequirementErrorMessage.NOT_ASSIGNED_TO_REQUIREMENT);
                }
            }
            case ADMIN -> {
                if (!comment.getUser().getId().equals(currentUser.getId())) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.ONLY_UPDATE_OWN_COMMENTS);
                }
                // Debe ser admin del workflow
                if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
                    throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
                }
            }
            case SUPER_ADMIN -> {
                if (!comment.getUser().getId().equals(currentUser.getId())) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.ONLY_UPDATE_OWN_COMMENTS);
                }
                if (comment.getType() != CommentType.INTERNAL) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.SUPER_ADMIN_ONLY_UPDATE_INTERNAL_COMMENTS);
                }
                // Debe ser SUPER_ADMIN de la área correspondiente
                if (!workflow.getArea().getCreatedBy().getId().equals(currentUser.getId())) {
                    throw new CommentOperationNotAllowedException(CommentErrorMessage.NOT_AREA_SUPER_ADMIN);
                }
            }
            default -> throw new CommentOperationNotAllowedException(CommentErrorMessage.ROLE_NOT_ALLOWED_TO_UPDATE);
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
