package pm.dev.code.requirements_management_backend.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.workflows.CreateWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.UpdateWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.WorkflowResponse;
import pm.dev.code.requirements_management_backend.entities.OrganizationalArea;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.entities.Workflow;
import pm.dev.code.requirements_management_backend.enums.Role;
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
            throw new AccessDeniedException("Access denied");
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
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found"));

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            return mapToResponse(workflow);
        }

        if (currentUser.getRol() == Role.ADMIN && workflow.getAdministrator().getId().equals(currentUser.getId())) {
            return mapToResponse(workflow);
        }

        if (currentUser.getRol() == Role.USUARIO && workflow.getUsers().contains(currentUser)) {
            return mapToResponse(workflow);
        }

        throw new AccessDeniedException("Access denied to this workflow");
    }

    @Override
    public WorkflowResponse createWorkflow(CreateWorkflowRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can create workflows");
        }

//        System.out.println("ROL DEL USER: " + currentUser.getRol());
//        System.out.println("AUTH: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        OrganizationalArea area = organizationalAreaRepository.findById(request.areaId())
                .orElseThrow(() -> new EntityNotFoundException("Área no encontrada"));

        if (!area.getAdministrators().contains(currentUser)) {
            throw new AccessDeniedException(
                    "You are not assigned to this organizational area"
            );
        }

        Workflow workflow = new Workflow();
        workflow.setName(request.name());
        workflow.setAdministrator(currentUser);
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setActive(true);
        workflow.setArea(area);

//        System.out.println("Usuario: " + currentUser.getUsername() + ", rol: " + currentUser.getRol());
//        System.out.println("ID de área recibido: " + request.areaId());
//        System.out.println("Area encontrada: " + area.getName());

        Workflow savedWorkflow = workflowRepository.save(workflow);

        return mapToResponse(savedWorkflow);
    }

    @Override
    public WorkflowResponse updateWorkflow(Long id, UpdateWorkflowRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can update workflows");
        }

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found"));

        if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own workflows");
        }

        workflow.setName(request.name());
        workflow.setActive(request.active());

        if (request.areaId() != null) {
            throw new AccessDeniedException("ADMIN cannot change workflow area");
        }

        workflow.setUpdatedAt(LocalDateTime.now());

        Workflow savedWorkflow = workflowRepository.save(workflow);

        return mapToResponse(savedWorkflow);
    }

    @Override
    public void deleteWorkflow(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found"));

        if (currentUser.getRol() != Role.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can delete workflows");
        }

        if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot delete workflows created by another admin");
        }

        workflowRepository.delete(workflow);
    }

    @Override
    public void assignUsersToWorkflow(Long workflowId, List<Long> userIds) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can assign users");
        }

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found"));

        // Admin solo puede asignar en sus workflows
        if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not owner of this workflow");
        }

        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            if (user.getRol() != Role.USUARIO) {
                throw new IllegalArgumentException("Only USERS can be assigned to workflows");
            }

            // evita duplicados
            if (!workflow.getUsers().contains(user)) {
                workflow.getUsers().add(user);
            }
        }

        workflow.setUpdatedAt(LocalDateTime.now());
        workflowRepository.save(workflow);
    }

    @Override
    public void removeUserFromWorkflow(Long workflowId, Long userId) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can remove users");
        }

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found"));

        if (!workflow.getAdministrator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not owner of this workflow");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!workflow.getUsers().contains(user)) {
            throw new IllegalStateException("User is not assigned to this workflow");
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
