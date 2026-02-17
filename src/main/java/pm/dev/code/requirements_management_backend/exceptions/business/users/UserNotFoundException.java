package pm.dev.code.requirements_management_backend.exceptions.business.users;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(UserErrorMessage message) {
        super(
                message.message(),
                ErrorCode.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
    }
}
