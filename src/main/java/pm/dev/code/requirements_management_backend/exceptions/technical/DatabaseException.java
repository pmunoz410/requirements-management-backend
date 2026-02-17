package pm.dev.code.requirements_management_backend.exceptions.technical;

import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;
import pm.dev.code.requirements_management_backend.exceptions.base.TechnicalException;

public class DatabaseException extends TechnicalException {

    public DatabaseException() {
        super(
                TechnicalErrorMessage.DATABASE_ERROR.message(),
                ErrorCode.DB_ERROR
        );
    }
}
