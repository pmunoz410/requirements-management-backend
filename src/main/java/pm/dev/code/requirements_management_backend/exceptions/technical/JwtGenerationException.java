package pm.dev.code.requirements_management_backend.exceptions.technical;

import pm.dev.code.requirements_management_backend.exceptions.base.ErrorCode;
import pm.dev.code.requirements_management_backend.exceptions.base.TechnicalException;

public class JwtGenerationException extends TechnicalException {

    public JwtGenerationException() {
        super(
                TechnicalErrorMessage.JWT_GENERATION_ERROR.message(),
                ErrorCode.JWT_GENERATION_ERROR
        );
    }
}
