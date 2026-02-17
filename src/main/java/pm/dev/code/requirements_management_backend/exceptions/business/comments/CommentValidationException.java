package pm.dev.code.requirements_management_backend.exceptions.business.comments;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class CommentValidationException extends BusinessException {

    public CommentValidationException(CommentErrorMessage message) {
        super(
                message.message(),
                ErrorCode.REQUIREMENT_VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST
        );
    }
}
