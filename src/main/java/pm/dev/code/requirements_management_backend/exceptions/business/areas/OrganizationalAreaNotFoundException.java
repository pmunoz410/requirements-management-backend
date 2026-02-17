package pm.dev.code.requirements_management_backend.exceptions.business.areas;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class OrganizationalAreaNotFoundException extends BusinessException {

    public OrganizationalAreaNotFoundException(OrganizationalAreaErrorMessage message) {
        super(
                message.message(),
                ErrorCode.AREA_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
    }
}
