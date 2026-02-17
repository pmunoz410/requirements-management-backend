package pm.dev.code.requirements_management_backend.exceptions.business.auth;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class UnauthorizedAccessException extends BusinessException {

    public UnauthorizedAccessException(AuthErrorMessage message) {
        super(
                message.message(),
                ErrorCode.AUTH_UNAUTHORIZED_ACCESS,
                HttpStatus.FORBIDDEN
        );
    }
}
