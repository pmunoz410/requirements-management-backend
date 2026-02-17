package pm.dev.code.requirements_management_backend.exceptions.business.workflows;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class WorkflowValidationException extends BusinessException {

    public WorkflowValidationException(WorkflowErrorMessage message) {
        super(
                message.message(),
                ErrorCode.WORKFLOW_VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST
        );
    }
}
