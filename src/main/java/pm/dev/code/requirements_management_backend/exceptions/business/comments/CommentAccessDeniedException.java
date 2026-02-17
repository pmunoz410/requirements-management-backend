package pm.dev.code.requirements_management_backend.exceptions.business.comments;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class CommentAccessDeniedException extends BusinessException {

    public CommentAccessDeniedException(CommentErrorMessage message) {
        super(
                message.message(),
                ErrorCode.REQUIREMENT_ACCESS_DENIED,
                HttpStatus.FORBIDDEN
        );
    }
}
