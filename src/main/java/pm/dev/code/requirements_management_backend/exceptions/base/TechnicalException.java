package pm.dev.code.requirements_management_backend.exceptions.base;

import org.springframework.http.HttpStatus;

public abstract class TechnicalException extends AppException {

    protected TechnicalException(String message, ErrorCode errorCode) {
        super(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
