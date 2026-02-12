package pm.dev.code.requirements_management_backend.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.areas.CreateOrganizationalAreaRequest;
import pm.dev.code.requirements_management_backend.dto.areas.OrganizationalAreaResponse;
import pm.dev.code.requirements_management_backend.dto.areas.UpdateOrganizationalAreaRequest;
import pm.dev.code.requirements_management_backend.entities.OrganizationalArea;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.enums.Role;
import pm.dev.code.requirements_management_backend.repositories.IOrganizationalAreaRepository;
import pm.dev.code.requirements_management_backend.repositories.IUserRepository;
import pm.dev.code.requirements_management_backend.services.IOrganizationalAreaService;
import pm.dev.code.requirements_management_backend.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationalAreaServiceImpl implements IOrganizationalAreaService {

    private final IOrganizationalAreaRepository organizationalAreaRepository;
    private final IUserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    public List<OrganizationalAreaResponse> getAllAreas() {
        User currentUser = securityUtils.getCurrentUser();

        List<OrganizationalArea> areas = List.of();

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            areas = organizationalAreaRepository.findByCreatedBy_Id(currentUser.getId());
        }

        if (currentUser.getRol() == Role.ADMIN) {
            areas = organizationalAreaRepository.findByAdministrators_Id(currentUser.getId());
        }

        if (areas.isEmpty()) {
            throw new AccessDeniedException("User does not have access to organizational areas");
        }

        return areas.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrganizationalAreaResponse getAreaById(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        OrganizationalArea area = organizationalAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organizational area not found"));

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            return mapToResponse(area);
        }

        if (currentUser.getRol() == Role.ADMIN &&
                organizationalAreaRepository.existsByIdAndAdministrators_Id(id, currentUser.getId())) {
            return mapToResponse(area);
        }

        throw new AccessDeniedException("Access denied to this organizational area");
    }

    @Override
    public OrganizationalAreaResponse createArea(CreateOrganizationalAreaRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only SUPER_ADMIN can create organizational areas");
        }

        OrganizationalArea area = new OrganizationalArea();
        area.setName(request.name());
        area.setActive(true);
        area.setCreatedAt(LocalDateTime.now());
        area.setCreatedBy(currentUser);

        return mapToResponse(organizationalAreaRepository.save(area));
    }

    @Override
    public OrganizationalAreaResponse updateArea(Long id, UpdateOrganizationalAreaRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only SUPER_ADMIN can update organizational areas");
        }

        OrganizationalArea area = organizationalAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organizational area not found"));

        area.setName(request.name());
        area.setActive(request.active());
        area.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(organizationalAreaRepository.save(area));
    }

    @Override
    public void deleteArea(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only SUPER_ADMIN can delete organizational areas");
        }

        OrganizationalArea area = organizationalAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organizational area not found"));

        organizationalAreaRepository.delete(area);
    }

    @Override
    public void assignAdmins(Long areaId, List<Long> adminIds) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only SUPER_ADMIN can assign administrators");
        }

        OrganizationalArea area = organizationalAreaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Organizational area not found"));

        for (Long adminId : adminIds) {
            User admin = userRepository.findById(adminId)
                    .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

            if (admin.getRol() != Role.ADMIN) {
                throw new IllegalArgumentException("User is not an ADMIN");
            }

            if (area.getAdministrators().contains(admin)) {
                throw new IllegalStateException(
                        "The administrator is already associated with this area"
                );
            }

            area.getAdministrators().add(admin);
        }

        area.setUpdatedAt(LocalDateTime.now());
        organizationalAreaRepository.save(area);
    }

    @Override
    public void removeAdmin(Long areaId, Long adminId) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only SUPER_ADMIN can remove administrators");
        }

        OrganizationalArea area = organizationalAreaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Organizational area not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        if (!area.getAdministrators().contains(admin)) {
            throw new IllegalStateException(
                    "The administrator is not associated with this area"
            );
        }

        area.getAdministrators().remove(admin);
        area.setUpdatedAt(LocalDateTime.now());

        organizationalAreaRepository.save(area);
    }

    @Override
    public void changeAdminArea(Long adminId, Long fromAreaId, Long toAreaId) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only SUPER_ADMIN can change admin area");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        if (admin.getRol() != Role.ADMIN) {
            throw new IllegalArgumentException("User is not an ADMIN");
        }

        OrganizationalArea fromArea = organizationalAreaRepository.findById(fromAreaId)
                .orElseThrow(() -> new EntityNotFoundException("Source area not found"));

        OrganizationalArea toArea = organizationalAreaRepository.findById(toAreaId)
                .orElseThrow(() -> new EntityNotFoundException("Target area not found"));

        if (!fromArea.getAdministrators().contains(admin)) {
            throw new IllegalStateException(
                    "The administrator does not belong to the source area"
            );
        }

        fromArea.getAdministrators().remove(admin);
        toArea.getAdministrators().add(admin);

        fromArea.setUpdatedAt(LocalDateTime.now());
        toArea.setUpdatedAt(LocalDateTime.now());

        organizationalAreaRepository.save(fromArea);
        organizationalAreaRepository.save(toArea);
    }

    private OrganizationalAreaResponse mapToResponse(OrganizationalArea area) {
        return new OrganizationalAreaResponse(
                area.getId(),
                area.getName(),
                area.isActive(),
                area.getAdministrators()
                        .stream()
                        .map(User::getId)
                        .toList(),
                area.getCreatedBy() != null ? area.getCreatedBy().getId() : null,
                area.getCreatedAt(),
                area.getUpdatedAt()
        );
    }
}
