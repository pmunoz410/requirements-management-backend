package pm.dev.code.requirements_management_backend.exceptions.business.users;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class UserValidationException extends BusinessException {

    public UserValidationException(UserErrorMessage message) {
        super(
                message.message(),
                ErrorCode.USER_VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST
        );
    }
}
