package pm.dev.code.requirements_management_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.areas.CreateOrganizationalAreaRequest;
import pm.dev.code.requirements_management_backend.dto.areas.OrganizationalAreaResponse;
import pm.dev.code.requirements_management_backend.dto.areas.UpdateOrganizationalAreaRequest;
import pm.dev.code.requirements_management_backend.entities.OrganizationalArea;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.enums.Role;
import pm.dev.code.requirements_management_backend.exceptions.business.areas.*;
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

        if (currentUser.getRol() != Role.SUPER_ADMIN && currentUser.getRol() != Role.ADMIN) {
            throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.LIST_NOT_ALLOWED);
        }

        return areas.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrganizationalAreaResponse getAreaById(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN && currentUser.getRol() != Role.ADMIN) {
            throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.VIEW_NOT_ALLOWED);
        }

        OrganizationalArea area = organizationalAreaRepository.findById(id)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.AREA_NOT_FOUND));

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            return mapToResponse(area);
        }

        if (currentUser.getRol() == Role.ADMIN &&
                organizationalAreaRepository.existsByIdAndAdministrators_Id(id, currentUser.getId())) {
            return mapToResponse(area);
        }

        throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.VIEW_NOT_ALLOWED);
    }

    @Override
    public OrganizationalAreaResponse createArea(CreateOrganizationalAreaRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.CREATE_NOT_ALLOWED);
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
            throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.UPDATE_NOT_ALLOWED);
        }

        OrganizationalArea area = organizationalAreaRepository.findById(id)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.AREA_NOT_FOUND));

        area.setName(request.name());
        area.setActive(request.active());
        area.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(organizationalAreaRepository.save(area));
    }

    @Override
    public void deleteArea(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.DELETE_NOT_ALLOWED);
        }

        OrganizationalArea area = organizationalAreaRepository.findById(id)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.AREA_NOT_FOUND));

        organizationalAreaRepository.delete(area);
    }

    @Override
    public void assignAdmins(Long areaId, List<Long> adminIds) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.ASSIGN_ADMIN_NOT_ALLOWED);
        }

        OrganizationalArea area = organizationalAreaRepository.findById(areaId)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.AREA_NOT_FOUND));

        for (Long adminId : adminIds) {
            User admin = userRepository.findById(adminId)
                    .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.ADMIN_USER_NOT_FOUND));

            if (admin.getRol() != Role.ADMIN) {
                throw new OrganizationalAreaValidationException(OrganizationalAreaErrorMessage.USER_NOT_ADMIN_ROLE);
            }

            if (area.getAdministrators().contains(admin)) {
                throw new OrganizationalAreaConflictException(OrganizationalAreaErrorMessage.ADMIN_ALREADY_ASSIGNED);
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
            throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.REMOVE_ADMIN_NOT_ALLOWED);
        }

        OrganizationalArea area = organizationalAreaRepository.findById(areaId)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.AREA_NOT_FOUND));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.ADMIN_USER_NOT_FOUND));

        if (!area.getAdministrators().contains(admin)) {
            throw new OrganizationalAreaConflictException(OrganizationalAreaErrorMessage.ADMIN_NOT_ASSIGNED);
        }

        area.getAdministrators().remove(admin);
        area.setUpdatedAt(LocalDateTime.now());

        organizationalAreaRepository.save(area);
    }

    @Override
    public void changeAdminArea(Long adminId, Long fromAreaId, Long toAreaId) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage.CHANGE_ADMIN_AREA_NOT_ALLOWED);
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.ADMIN_USER_NOT_FOUND));

        if (admin.getRol() != Role.ADMIN) {
            throw new OrganizationalAreaValidationException(OrganizationalAreaErrorMessage.USER_NOT_ADMIN_ROLE);
        }

        OrganizationalArea fromArea = organizationalAreaRepository.findById(fromAreaId)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.AREA_NOT_FOUND));

        OrganizationalArea toArea = organizationalAreaRepository.findById(toAreaId)
                .orElseThrow(() -> new OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage.AREA_NOT_FOUND));

        if (!fromArea.getAdministrators().contains(admin)) {
            throw new OrganizationalAreaConflictException(OrganizationalAreaErrorMessage.ADMIN_NOT_IN_SOURCE_AREA);
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
