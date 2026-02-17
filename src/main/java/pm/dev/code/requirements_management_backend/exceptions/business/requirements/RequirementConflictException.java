package pm.dev.code.requirements_management_backend.exceptions.business.requirements;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class RequirementConflictException extends BusinessException {

    public RequirementConflictException(RequirementErrorMessage message) {
        super(
                message.message(),
                ErrorCode.REQUIREMENT_CONFLICT,
                HttpStatus.CONFLICT
        );
    }
}
