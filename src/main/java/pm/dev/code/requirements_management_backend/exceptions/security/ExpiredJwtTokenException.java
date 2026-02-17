package pm.dev.code.requirements_management_backend.exceptions.security;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class ExpiredJwtTokenException extends BusinessException {

    public ExpiredJwtTokenException(JwtErrorMessage message) {
        super(
                message.message(),
                ErrorCode.JWT_EXPIRED_TOKEN,
                HttpStatus.UNAUTHORIZED
        );
    }
}
