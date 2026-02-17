package pm.dev.code.requirements_management_backend.exceptions.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;

    protected AppException(String message, ErrorCode errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
