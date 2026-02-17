package pm.dev.code.requirements_management_backend.exceptions.technical;

import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;
import pm.dev.code.requirements_management_backend.exceptions.base.TechnicalException;

public class DataIntegrityException extends TechnicalException {

    public DataIntegrityException() {
        super(
                TechnicalErrorMessage.INTEGRITY_ERROR.message(),
                ErrorCode.DB_ERROR
        );
    }
}
