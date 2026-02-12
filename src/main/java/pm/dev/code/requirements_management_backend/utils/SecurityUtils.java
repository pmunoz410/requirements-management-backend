package pm.dev.code.requirements_management_backend.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.security.UserDetailsImpl;

@Component
public class SecurityUtils {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser();
        }

        return null;
    }
}
