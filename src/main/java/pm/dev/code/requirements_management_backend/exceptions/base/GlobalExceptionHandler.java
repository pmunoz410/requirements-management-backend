package pm.dev.code.requirements_management_backend.exceptions.base;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pm.dev.code.requirements_management_backend.exceptions.technical.UnexpectedTechnicalException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiErrorBuilder apiErrorBuilder;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        if (ex instanceof BusinessException) {
            log.warn("Business exception: {}", ex.getMessage());
        } else {
            log.error("Technical exception occurred", ex);
        }

        return ResponseEntity.status(ex.getStatus())
                .body(apiErrorBuilder.build(ex, request, ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected system error occurred", ex);

        UnexpectedTechnicalException wrapped = new UnexpectedTechnicalException();

        return ResponseEntity.status(wrapped.getStatus())
                .body(apiErrorBuilder.build(wrapped, request, ex));
    }
}
