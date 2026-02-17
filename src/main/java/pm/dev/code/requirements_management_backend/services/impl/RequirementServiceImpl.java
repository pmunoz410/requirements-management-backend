package pm.dev.code.requirements_management_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.requirements.*;
import pm.dev.code.requirements_management_backend.entities.Comment;
import pm.dev.code.requirements_management_backend.entities.Requirement;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.entities.Workflow;
import pm.dev.code.requirements_management_backend.enums.CommentType;
import pm.dev.code.requirements_management_backend.enums.RequirementStatus;
import pm.dev.code.requirements_management_backend.enums.Role;
import pm.dev.code.requirements_management_backend.exceptions.business.requirements.*;
import pm.dev.code.requirements_management_backend.exceptions.business.users.UserErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.business.users.UserNotFoundException;
import pm.dev.code.requirements_management_backend.exceptions.business.workflows.WorkflowErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.business.workflows.WorkflowNotFoundException;
import pm.dev.code.requirements_management_backend.exceptions.business.workflows.WorkflowOperationNotAllowedException;
import pm.dev.code.requirements_management_backend.repositories.ICommentRepository;
import pm.dev.code.requirements_management_backend.repositories.IRequirementRepository;
import pm.dev.code.requirements_management_backend.repositories.IUserRepository;
import pm.dev.code.requirements_management_backend.repositories.IWorkflowRepository;
import pm.dev.code.requirements_management_backend.services.IRequirementService;
import pm.dev.code.requirements_management_backend.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequirementServiceImpl implements IRequirementService {

    private final IRequirementRepository requirementRepository;
    private final IWorkflowRepository workflowRepository;
    private final IUserRepository userRepository;
    private final ICommentRepository commentRepository;
    private final SecurityUtils securityUtils;

    @Override
    public List<RequirementResponse> getAllRequirements() {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN
                && currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.USUARIO) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.LIST_NOT_ALLOWED);
        }

        List<Requirement> requirements = List.of();

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            requirements = requirementRepository.findAll();
        }

        if (currentUser.getRol() == Role.ADMIN) {
            requirements = requirementRepository.findByWorkflowAdministrator(currentUser);
        }

        if (currentUser.getRol() == Role.USUARIO) {
            requirements = requirementRepository.findByAssignee(currentUser)
                    .stream()
                    .filter(Requirement::isActive)
                    .toList();
        }

        return requirements.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RequirementResponse getRequirementById(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            return mapToResponse(requirement);
        }

        if (currentUser.getRol() == Role.ADMIN && requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
            return mapToResponse(requirement);
        }

        if (currentUser.getRol() == Role.USUARIO && currentUser.equals(requirement.getAssignee()) &&
                requirement.isActive()) {
            return mapToResponse(requirement);
        }

        throw new RequirementOperationNotAllowedException(RequirementErrorMessage.VIEW_NOT_ALLOWED);
    }

    @Override
    public List<RequirementResponse> getRequirementsByWorkflow(Long workflowId) {
        User currentUser = securityUtils.getCurrentUser();

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(WorkflowErrorMessage.WORKFLOW_NOT_FOUND));

//        if (currentUser.getRol() != Role.SUPER_ADMIN && currentUser.getRol() != Role.ADMIN &&
//                !workflow.getAdministrator().getId().equals(currentUser.getId())) {
//            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.VIEW_NOT_ALLOWED);
//        }

        if (currentUser.getRol() == Role.ADMIN && !workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.VIEW_WORKFLOW_REQUIREMENTS_NOT_ALLOWED);
        }

        if (currentUser.getRol() == Role.USUARIO && !workflow.getUsers().contains(currentUser)) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.VIEW_WORKFLOW_REQUIREMENTS_NOT_ALLOWED);
        }

        List<Requirement> requirements  = List.of();

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            requirements = requirementRepository.findByWorkflow(workflow);
        }

        if (currentUser.getRol() == Role.ADMIN && workflow.getAdministrator().getId().equals(currentUser.getId())) {
            requirements = requirementRepository.findByWorkflow(workflow);
        }

        if (currentUser.getRol() == Role.USUARIO) {
            requirements = requirementRepository.findByWorkflowAndAssignee(workflow, currentUser)
                    .stream()
                    .filter(Requirement::isActive)
                    .toList();
        }

        return requirements.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RequirementResponse createRequirement(Long workflowId,CreateRequirementRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.CREATE_NOT_ALLOWED);
        }

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(WorkflowErrorMessage.WORKFLOW_NOT_FOUND));

        if (!workflow.getAdministrator().getId().equals(currentUser.getId()))
            throw new WorkflowNotFoundException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);

        Requirement requirement = new Requirement();
        requirement.setTitle(request.title());
        requirement.setDescription(request.description());
        requirement.setPriority(request.priority());
        requirement.setClassification(request.classification());
        requirement.setStatus(RequirementStatus.PENDIENTE);
        requirement.setWorkflow(workflow);
        requirement.setActive(true);
        requirement.setCreatedAt(LocalDateTime.now());

        Requirement saveRequirement = requirementRepository.save(requirement);

        return mapToResponse(saveRequirement);
    }

    @Override
    public RequirementResponse updateRequirementDetails(Long id, UpdateRequirementDetailsRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.UPDATE_NOT_ALLOWED);
        }

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        requirement.setTitle(request.title());
        requirement.setDescription(request.description());
        requirement.setPriority(request.priority());
        requirement.setClassification(request.classification());
        requirement.setUpdatedAt(LocalDateTime.now());

        Requirement saveRequirement = requirementRepository.save(requirement);

        return mapToResponse(saveRequirement);
    }

    private Requirement getOwnedRequirement(Long id, User admin) {
        if (admin.getRol() != Role.ADMIN)
            throw new AccessDeniedException("Only ADMIN allowed");

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (!requirement.getWorkflow().getAdministrator().getId().equals(admin.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        return requirement;
    }

    @Override
    public RequirementResponse updateRequirementStatusByAdmin(Long id, UpdateRequirementStatusByAdminRequest request) {
        User currentUser = securityUtils.getCurrentUser();
//        Requirement requirement = getOwnedRequirement(id, securityUtils.getCurrentUser());

        if (currentUser.getRol() != Role.ADMIN) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.UPDATE_NOT_ALLOWED);
        }

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        if (requirement.getStatus() == RequirementStatus.FINALIZADO) {
            throw new RequirementValidationException(RequirementErrorMessage.FINALIZED_REQUIREMENT);
        }

        if (request.status() == RequirementStatus.REQUIERE_CAMBIOS &&
                (request.comment() == null || request.comment().isBlank())) {
            throw new RequirementValidationException(RequirementErrorMessage.COMMENT_REQUIRED);
        }

        requirement.setStatus(request.status());
        requirement.setUpdatedAt(LocalDateTime.now());

        if (request.status() == RequirementStatus.FINALIZADO) {
            requirement.setClosedAt(LocalDateTime.now());
        }

        Requirement saveRequirement = requirementRepository.save(requirement);

        if (request.status() == RequirementStatus.REQUIERE_CAMBIOS) {
            Comment comment = new Comment();
            comment.setRequirement(saveRequirement);
            comment.setUser(currentUser);
            comment.setContent(request.comment());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setType(CommentType.PUBLIC);

            commentRepository.save(comment);
        }

        return mapToResponse(saveRequirement);
    }

    @Override
    public RequirementResponse updateRequirementStatusByUser(Long id, UpdateRequirementStatusRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (requirement.getAssignee() == null || !requirement.getAssignee().getId().equals(currentUser.getId())) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.NOT_ASSIGNED_TO_REQUIREMENT);
        }

        validateUserStatusTransition(requirement.getStatus(), request.status());
        requirement.setStatus(request.status());
        requirement.setUpdatedAt(LocalDateTime.now());

        Requirement saveRequirement = requirementRepository.save(requirement);

        return mapToResponse(saveRequirement);
    }

    @Override
    public void deleteRequirement(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.DELETE_NOT_ALLOWED);
        }

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        requirementRepository.delete(requirement);
    }

    @Override
    public RequirementResponse assignRequirement(Long requirementId, Long userId) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.ASSIGN_NOT_ALLOWED);
        }

        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

        if (user.getRol() != Role.USUARIO) {
            throw new RequirementValidationException(RequirementErrorMessage.USER_NOT_USER_ROLE);
        }

        Workflow workflow = requirement.getWorkflow();

        if (!workflow.getUsers().contains(user)) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.USER_NOT_IN_WORKFLOW);
        }

        if (requirement.getAssignee() != null) {
            if (requirement.getAssignee().getId().equals(user.getId())) {
                throw new RequirementConflictException(RequirementErrorMessage.USER_ALREADY_ASSIGNED);
            } else {
                throw new RequirementConflictException(RequirementErrorMessage.REQUIREMENT_ALREADY_ASSIGNED);
            }
        }

        requirement.setAssignee(user);
        requirement.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(requirementRepository.save(requirement));
    }

    @Override
    public RequirementResponse unassignRequirement(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.UNASSIGN_NOT_ALLOWED);
        }

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        if (requirement.getAssignee() == null) {
            throw new RequirementConflictException(RequirementErrorMessage.REQUIREMENT_NOT_ASSIGNED);
        }

        requirement.setAssignee(null);
        requirement.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(requirementRepository.save(requirement));
    }

    @Override
    public void enableRequirement(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new RequirementConflictException(RequirementErrorMessage.ENABLE_NOT_ALLOWED);
        }

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (requirement.isActive()) {
            throw new RequirementConflictException(RequirementErrorMessage.REQUIREMENT_ALREADY_ENABLED);
        }

        if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        requirement.setActive(true);
        requirement.setUpdatedAt(LocalDateTime.now());
        requirementRepository.save(requirement);
    }

    @Override
    public void disableRequirement(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new RequirementOperationNotAllowedException(RequirementErrorMessage.DISABLE_NOT_ALLOWED);
        }

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException(RequirementErrorMessage.REQUIREMENT_NOT_FOUND));

        if (!requirement.isActive()) {
            throw new RequirementConflictException(RequirementErrorMessage.REQUIREMENT_ALREADY_DISABLED);
        }

        if (!requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        requirement.setActive(false);
        requirement.setUpdatedAt(LocalDateTime.now());
        requirementRepository.save(requirement);
    }

    private void validateUserStatusTransition(RequirementStatus current, RequirementStatus next) {
        if (current == RequirementStatus.PENDIENTE && next == RequirementStatus.EN_DESARROLLO) return;
        if (current == RequirementStatus.EN_DESARROLLO && next == RequirementStatus.EN_PRUEBAS) return;

        throw new RequirementOperationNotAllowedException(RequirementErrorMessage.INVALID_STATUS_TRANSITION);
    }

    private RequirementResponse mapToResponse(Requirement requirement) {
        return new RequirementResponse(
                requirement.getId(),
                requirement.getTitle(),
                requirement.getDescription(),
                requirement.getStatus(),
                requirement.getPriority(),
                requirement.getClassification(),
                requirement.getWorkflow().getId(),
                requirement.getAssignee() != null ? requirement.getAssignee().getId() : null,
                requirement.isActive(),
                requirement.getCreatedAt(),
                requirement.getUpdatedAt()
        );
    }
}
