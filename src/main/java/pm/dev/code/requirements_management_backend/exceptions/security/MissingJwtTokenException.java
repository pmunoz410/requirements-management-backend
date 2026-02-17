package pm.dev.code.requirements_management_backend.exceptions.security;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class MissingJwtTokenException extends BusinessException {

    public MissingJwtTokenException(JwtErrorMessage message) {
        super(
                message.message(),
                ErrorCode.JWT_MISSING_TOKEN,
                HttpStatus.UNAUTHORIZED
        );
    }
}
