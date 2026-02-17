package pm.dev.code.requirements_management_backend.exceptions.business.auth;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException(AuthErrorMessage message) {
        super(
                message.message(),
                ErrorCode.AUTH_INVALID_CREDENTIALS,
                HttpStatus.UNAUTHORIZED
        );
    }
}
