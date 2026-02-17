package pm.dev.code.requirements_management_backend.exceptions.technical;

import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;
import pm.dev.code.requirements_management_backend.exceptions.base.TechnicalException;

public class UnexpectedTechnicalException extends TechnicalException {

    public UnexpectedTechnicalException() {
        super(
                TechnicalErrorMessage.INTERNAL_ERROR.message(),
                ErrorCode.INTERNAL_ERROR
        );
    }
}
