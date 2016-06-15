package de.hohenheim.security;

import de.hohenheim.model.LearningGroup;
import de.hohenheim.model.LearningGroupRepository;
import de.hohenheim.model.SopraUser;
import de.hohenheim.model.SopraUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;


@Transactional
@Component
public class TestSetup implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SopraUserRepository userRepository;
    @Autowired
    private LearningGroupRepository learningGroupRepository;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Collection<GrantedAuthority> authsAdmin = new ArrayList<>();
        authsAdmin.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        userDetailsManager.createUser(new User("admin", passwordEncoder.encode("admin"), authsAdmin));

        Collection<GrantedAuthority> authsHans = new ArrayList<>();
        authsHans.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetailsManager.createUser(new User("hans", passwordEncoder.encode("hugo"), authsHans));

        SopraUser user1 = new SopraUser();
        user1.setUsername("admin");
        userRepository.save(user1);

        SopraUser user2 = new SopraUser();
        user2.setUsername("hans");
        userRepository.save(user2);
    }


}