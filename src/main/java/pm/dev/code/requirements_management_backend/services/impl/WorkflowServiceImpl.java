package pm.dev.code.requirements_management_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.workflows.CreateWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.UpdateWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.WorkflowResponse;
import pm.dev.code.requirements_management_backend.entities.OrganizationalArea;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.entities.Workflow;
import pm.dev.code.requirements_management_backend.enums.Role;
import pm.dev.code.requirements_management_backend.exceptions.business.areas.OrganizationalAreaErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.business.areas.OrganizationalAreaNotFoundException;
import pm.dev.code.requirements_management_backend.exceptions.business.users.UserErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.business.users.UserNotFoundException;
import pm.dev.code.requirements_management_backend.exceptions.business.workflows.*;
import pm.dev.code.requirements_management_backend.repositories.IOrganizationalAreaRepository;
import pm.dev.code.requirements_management_backend.repositories.IUserRepository;
import pm.dev.code.requirements_management_backend.repositories.IWorkflowRepository;
import pm.dev.code.requirements_management_backend.services.IWorkflowService;
import pm.dev.code.requirements_management_backend.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements IWorkflowService {

    private final IWorkflowRepository workflowRepository;
    private final IOrganizationalAreaRepository organizationalAreaRepository;
    private final IUserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    public List<WorkflowResponse> getAllWorkflows() {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN
                && currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.USUARIO) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.LIST_NOT_ALLOWED);
        }

        List<Workflow> workflows = List.of();

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            workflows = workflowRepository.findAll();
        }

        if (currentUser.getRol() == Role.ADMIN) {
            workflows = workflowRepository.findByAdministrator(currentUser);
        }

        if (currentUser.getRol() == Role.USUARIO) {
            workflows = workflowRepository.findByUsersContaining(currentUser);
        }

        return workflows.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public WorkflowResponse getWorkflowById(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(WorkflowErrorMessage.WORKFLOW_NOT_FOUND));

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            return mapToResponse(workflow);
        }

        if (currentUser.getRol() == Role.ADMIN && workflow.getAdministrator().getId().equals(currentUser.getId())) {
            return mapToResponse(workflow);
        }

        if (currentUser.getRol() == Role.USUARIO && workflow.getUsers().contains(currentUser)) {
            return mapToResponse(workflow);
        }

        throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.VIEW_NOT_ALLOWED);
    }

    @Override
    public WorkflowResponse createWorkflow(CreateWorkflowRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.CREATE_NOT_ALLOWED);
        }

        OrganizationalArea area = organizationalAreaRepository.findById(request.areaId())
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.AREA_NOT_FOUND));

        if (!area.getAdministrators().contains(currentUser)) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        Workflow workflow = new Workflow();
        workflow.setName(request.name());
        workflow.setAdministrator(currentUser);
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setActive(true);
        workflow.setArea(area);

        Workflow savedWorkflow = workflowRepository.save(workflow);

        return mapToResponse(savedWorkflow);
    }

    @Override
    public WorkflowResponse updateWorkflow(Long id, UpdateWorkflowRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.UPDATE_NOT_ALLOWED);
        }

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(WorkflowErrorMessage.WORKFLOW_NOT_FOUND));

        if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        workflow.setName(request.name());
        workflow.setActive(request.active());

        if (request.areaId() != null) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.CHANGE_AREA_NOT_ALLOWED);
        }

        workflow.setUpdatedAt(LocalDateTime.now());

        Workflow savedWorkflow = workflowRepository.save(workflow);

        return mapToResponse(savedWorkflow);
    }

    @Override
    public void deleteWorkflow(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(WorkflowErrorMessage.WORKFLOW_NOT_FOUND));

        if (currentUser.getRol() != Role.ADMIN) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.DELETE_NOT_ALLOWED);
        }

        if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        workflowRepository.delete(workflow);
    }

    @Override
    public void assignUsersToWorkflow(Long workflowId, List<Long> userIds) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.ASSIGN_USERS_NOT_ALLOWED);
        }

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(WorkflowErrorMessage.WORKFLOW_NOT_FOUND));

        // Admin solo puede asignar en sus workflows
        if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

            if (user.getRol() != Role.USUARIO) {
                throw new WorkflowValidationException(WorkflowErrorMessage.USER_NOT_USER_ROLE);
            }

            if (workflow.getUsers().contains(user)) {
                throw new WorkflowConflictException(WorkflowErrorMessage.USER_ALREADY_ASSIGNED);
            }

            workflow.getUsers().add(user);
        }

        workflow.setUpdatedAt(LocalDateTime.now());
        workflowRepository.save(workflow);
    }

    @Override
    public void removeUserFromWorkflow(Long workflowId, Long userId) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.REMOVE_USERS_NOT_ALLOWED);
        }

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(WorkflowErrorMessage.WORKFLOW_NOT_FOUND));

        if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new WorkflowOperationNotAllowedException(WorkflowErrorMessage.NOT_WORKFLOW_OWNER);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

        if (!workflow.getUsers().contains(user)) {
            throw new WorkflowConflictException(WorkflowErrorMessage.USER_NOT_ASSIGNED);
        }

        workflow.getUsers().remove(user);
        workflow.setUpdatedAt(LocalDateTime.now());
        workflowRepository.save(workflow);
    }

    private WorkflowResponse mapToResponse(Workflow workflow) {
        return new WorkflowResponse(
                workflow.getId(),
                workflow.getName(),
                workflow.getArea().getId(),
                workflow.getAdministrator().getId(),
                workflow.isActive(),
                workflow.getCreatedAt(),
                workflow.getUpdatedAt()
        );
    }
}
