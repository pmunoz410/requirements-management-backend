package pm.dev.code.requirements_management_backend.exceptions.base;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ApiErrorBuilder {

    @Value("${app.errors.include-debug-info:false}")
    private boolean includeDebugInfo;

    public ApiErrorResponse build(AppException ex, HttpServletRequest request, Exception original) {
        if (includeDebugInfo) {
            return new ApiErrorResponse(
                    LocalDateTime.now(),
                    ex.getStatus().value(),
                    ex.getStatus().name(),
                    ex.getMessage(),
                    request.getRequestURI(),
                    request.getMethod(),
                    original.getClass().getSimpleName(),
                    ex.getErrorCode().code()
            );
        }

        return new ApiErrorResponse(
                null,
                ex.getStatus().value(),
                null,
                ex.getMessage(),
                null,
                null,
                null,
                ex.getErrorCode().code()
        );
    }
}
