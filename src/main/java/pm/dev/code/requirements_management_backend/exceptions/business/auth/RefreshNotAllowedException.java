package pm.dev.code.requirements_management_backend.exceptions.business.auth;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class RefreshNotAllowedException extends BusinessException {

    public RefreshNotAllowedException(AuthErrorMessage message) {
        super(
                message.message(),
                ErrorCode.AUTH_REFRESH_NOT_ALLOWED,
                HttpStatus.UNAUTHORIZED
        );
    }
}
