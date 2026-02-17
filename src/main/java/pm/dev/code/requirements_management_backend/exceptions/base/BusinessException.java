package pm.dev.code.requirements_management_backend.exceptions.base;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends AppException {

    protected BusinessException(String message, ErrorCode errorCode, HttpStatus status) {
        super(message, errorCode, status);
    }
}
