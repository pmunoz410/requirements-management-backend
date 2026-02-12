package pm.dev.code.requirements_management_backend.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pm.dev.code.requirements_management_backend.dto.requirements.*;
import pm.dev.code.requirements_management_backend.entities.Comment;
import pm.dev.code.requirements_management_backend.entities.Requirement;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.entities.Workflow;
import pm.dev.code.requirements_management_backend.enums.CommentType;
import pm.dev.code.requirements_management_backend.enums.RequirementStatus;
import pm.dev.code.requirements_management_backend.enums.Role;
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
            throw new AccessDeniedException("Access denied");
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
                .orElseThrow(() -> new EntityNotFoundException("Requirement not found"));

        if (currentUser.getRol() == Role.SUPER_ADMIN) return mapToResponse(requirement);

        if (currentUser.getRol() == Role.ADMIN &&
                requirement.getWorkflow().getAdministrator().getId().equals(currentUser.getId()))
            return mapToResponse(requirement);

        if (currentUser.getRol() == Role.USUARIO && currentUser.equals(requirement.getAssignee()) &&
                requirement.isActive())
            return mapToResponse(requirement);

        throw new AccessDeniedException("Access denied");
    }

    @Override
    public List<RequirementResponse> getRequirementsByWorkflow(Long workflowId) {
        User currentUser = securityUtils.getCurrentUser();

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found"));

        if (currentUser.getRol() != Role.SUPER_ADMIN &&
                currentUser.getRol() != Role.ADMIN &&
                !workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied");
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

        if (currentUser.getRol() != Role.ADMIN)
            throw new AccessDeniedException("Only ADMIN can create requirements");

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found"));

        if (!workflow.getAdministrator().getId().equals(currentUser.getId()))
            throw new AccessDeniedException("You are not owner of this workflow");

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

        Requirement requirement = getOwnedRequirement(id, currentUser);

        requirement.setTitle(request.title());
        requirement.setDescription(request.description());
        requirement.setPriority(request.priority());
        requirement.setClassification(request.classification());
        requirement.setUpdatedAt(LocalDateTime.now());

        Requirement saveRequirement = requirementRepository.save(requirement);

        return mapToResponse(saveRequirement);
    }

    @Override
    public RequirementResponse updateRequirementStatusByAdmin(Long id, UpdateRequirementStatusByAdminRequest request) {

        User currentUser = securityUtils.getCurrentUser();
        Requirement requirement = getOwnedRequirement(id, securityUtils.getCurrentUser());

        if (requirement.getStatus() == RequirementStatus.FINALIZADO) {
            throw new IllegalStateException("Finalized requirements cannot change status");
        }

        if (request.status() == RequirementStatus.REQUIERE_CAMBIOS &&
                (request.comment() == null || request.comment().isBlank())) {
            throw new IllegalStateException("Comment is required when status is REQUIERE_CAMBIOS");
        }

        requirement.setStatus(request.status());
        requirement.setUpdatedAt(LocalDateTime.now());

        if (request.status() == RequirementStatus.FINALIZADO)
            requirement.setClosedAt(LocalDateTime.now());

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
                .orElseThrow(() -> new EntityNotFoundException("Requirement not found"));

        if (requirement.getAssignee() == null || !requirement.getAssignee().getId().equals(currentUser.getId()))
            throw new AccessDeniedException("Not assigned to this requirement");

        validateUserStatusTransition(requirement.getStatus(), request.status());
        requirement.setStatus(request.status());
        requirement.setUpdatedAt(LocalDateTime.now());

        Requirement saveRequirement = requirementRepository.save(requirement);

        return mapToResponse(saveRequirement);
    }

    @Override
    public void deleteRequirement(Long id) {
        Requirement requirement = getOwnedRequirement(id, securityUtils.getCurrentUser());
        requirementRepository.delete(requirement);
    }

    @Override
    public RequirementResponse assignRequirement(Long reqId, Long userId) {
        Requirement requirement = getOwnedRequirement(reqId, securityUtils.getCurrentUser());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRol() != Role.USUARIO)
            throw new IllegalArgumentException("Only USERS can be assigned");

        Workflow workflow = requirement.getWorkflow();

        if (!workflow.getUsers().contains(user)) {
            throw new IllegalArgumentException(
                    "User is not assigned to this workflow"
            );
        }

        if (requirement.getAssignee() != null) {
            if (requirement.getAssignee().getId().equals(user.getId())) {
                throw new IllegalStateException("Este usuario ya está asignado a este requerimiento");
            } else {
                throw new IllegalStateException("Ya hay un usuario asignado a este requerimiento");
            }
        }

        requirement.setAssignee(user);
        requirement.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(requirementRepository.save(requirement));
    }

    @Override
    public RequirementResponse unassignRequirement(Long id) {
        Requirement requirement = getOwnedRequirement(id, securityUtils.getCurrentUser());

        if (requirement.getAssignee() == null) {
            throw new IllegalStateException("No hay usuario asignado a este requerimiento");
        }

        requirement.setAssignee(null);
        requirement.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(requirementRepository.save(requirement));
    }

    @Override
    public void enableRequirement(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Requirement not found"));

        if (requirement.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requirement is already enabled");
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        requirement.setActive(true);
        requirement.setUpdatedAt(LocalDateTime.now());
        requirementRepository.save(requirement);
    }

    @Override
    public void disableRequirement(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Requirement requirement = getOwnedRequirement(id, currentUser);

        if (!requirement.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requirement is already disabled");
        }

        requirement.setActive(false);
        requirement.setUpdatedAt(LocalDateTime.now());
        requirementRepository.save(requirement);
    }

    private Requirement getOwnedRequirement(Long id, User admin) {
        if (admin.getRol() != Role.ADMIN)
            throw new AccessDeniedException("Only ADMIN allowed");

        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Requirement not found"));

        if (!requirement.getWorkflow().getAdministrator().getId().equals(admin.getId()))
            throw new AccessDeniedException("Not owner of workflow");

        return requirement;
    }

    private void validateUserStatusTransition(RequirementStatus current, RequirementStatus next) {
        if (current == RequirementStatus.PENDIENTE && next == RequirementStatus.EN_DESARROLLO) return;
        if (current == RequirementStatus.EN_DESARROLLO && next == RequirementStatus.EN_PRUEBAS) return;

        throw new AccessDeniedException("Invalid status transition");
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
