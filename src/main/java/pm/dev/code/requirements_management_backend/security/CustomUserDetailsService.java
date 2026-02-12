package pm.dev.code.requirements_management_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.repositories.IUserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Cargando usuario: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        System.out.println("Usuario encontrado: " + user.getUsername() + ", activo: " + user.isActive());
        if (!user.isActive()) {
            throw new DisabledException("User is disabled");
        }

        return new UserDetailsImpl(user);
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUsername())
//                .password(user.getPassword())
//                .roles(user.getRol().name())
//                .disabled(!user.isActivo())
//                .build();
    }
}
