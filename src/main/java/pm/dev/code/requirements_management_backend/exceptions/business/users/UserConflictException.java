package pm.dev.code.requirements_management_backend.exceptions.business.users;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class UserConflictException extends BusinessException {

    public UserConflictException(UserErrorMessage message) {
        super(
                message.message(),
                ErrorCode.USER_CONFLICT,
                HttpStatus.CONFLICT
        );
    }
}
