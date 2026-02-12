package pm.dev.code.requirements_management_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pm.dev.code.requirements_management_backend.entities.OrganizationalArea;

import java.util.List;

public interface IOrganizationalAreaRepository extends JpaRepository<OrganizationalArea, Long> {
    List<OrganizationalArea> findByAdministrators_Id(Long adminId);
    boolean existsByIdAndAdministrators_Id(Long areaId, Long adminId);
    List<OrganizationalArea> findByCreatedBy_Id(Long superAdminId);
}
