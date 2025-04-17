package init;

import com.sandrohenrique.secret_santa.domain.user.Role;
import com.sandrohenrique.secret_santa.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        if (!roleRepository.existsById(1L)) {
            roleRepository.save(new Role(1L, "ADMIN"));

        }
        if (!roleRepository.existsById(2L)) {
            roleRepository.save(new Role(2L, "USER"));
        }
    }
}
