package pm.dev.code.requirements_management_backend.exceptions.business.areas;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class OrganizationalAreaOperationNotAllowedException extends BusinessException {

    public OrganizationalAreaOperationNotAllowedException(OrganizationalAreaErrorMessage message) {
        super(
                message.message(),
                ErrorCode.AREA_ACCESS_DENIED,
                HttpStatus.FORBIDDEN
        );
    }
}
