package pm.dev.code.requirements_management_backend.exceptions.technical;

import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;
import pm.dev.code.requirements_management_backend.exceptions.base.TechnicalException;

public class ExternalServiceException extends TechnicalException {

    public ExternalServiceException() {
        super(
                TechnicalErrorMessage.EXTERNAL_SERVICE_ERROR.message(),
                ErrorCode.EXTERNAL_SERVICE_ERROR
        );
    }
}
