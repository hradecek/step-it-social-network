package cz.stepit.social;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import cz.stepit.social.entities.Member;
import cz.stepit.social.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class TestUsersCreator implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUsersCreator.class);

    protected final Map<String, Consumer<String>> DDL_ACTIONS = Map.of(
            "create", this::createUser,
            "create-drop", this::dropCreateUser);

    protected final PasswordEncoder passwordEncoder;
    protected final MemberRepository memberRepository;
    protected final UserDetailsManager userDetailsManager;
    protected final String ddlAuto;

    public TestUsersCreator(
            PasswordEncoder passwordEncoder,
            MemberRepository memberRepository,
            UserDetailsManager userDetailsManager,
            @Value("${spring.jpa.hibernate.ddl-auto}") String ddlAuto) {

        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.userDetailsManager = userDetailsManager;
        this.ddlAuto = ddlAuto;
    }

    @Override
    public void run(String... args) {
        final var users = Stream.of("ivo", "joe");
        final var ddlAction = DDL_ACTIONS.get(ddlAuto);
        if (ddlAction != null) {
            users.forEach(ddlAction);
        }
    }

    protected void dropCreateUser(String username) {
        dropUser(username);
        createUser(username);
    }

    protected void dropUser(String username) {
        userDetailsManager.deleteUser(username);
        memberRepository.findByUsername(username).ifPresent(member -> memberRepository.deleteById(member.getId()));
        LOGGER.info("DROP {}", username);
    }

    protected void createUser(String username) {
        userDetailsManager.createUser(createUserDetails(username, "pass"));
        memberRepository.save(new Member(username));
        LOGGER.info("CREATE {}", username);
    }

    protected UserDetails createUserDetails(String username, String password) {
        return User
                .builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .roles("MEMBER")
                .build();
    }
}
