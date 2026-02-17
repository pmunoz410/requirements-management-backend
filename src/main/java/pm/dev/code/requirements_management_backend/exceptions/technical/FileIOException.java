package pm.dev.code.requirements_management_backend.exceptions.technical;

import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;
import pm.dev.code.requirements_management_backend.exceptions.base.TechnicalException;

public class FileIOException extends TechnicalException {

    public FileIOException() {
        super(
                TechnicalErrorMessage.FILE_IO_ERROR.message(),
                ErrorCode.INTERNAL_ERROR
        );
    }
}
