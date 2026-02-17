package pm.dev.code.requirements_management_backend.exceptions.business.workflows;

import org.springframework.http.HttpStatus;
import pm.dev.code.requirements_management_backend.exceptions.base.BusinessException;
import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;

public class WorkflowNotFoundException extends BusinessException {

    public WorkflowNotFoundException(WorkflowErrorMessage message) {
        super(
                message.message(),
                ErrorCode.WORKFLOW_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
    }
}
