package cz.stepit.social.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import cz.stepit.social.entities.Member;
import cz.stepit.social.repository.MemberRepository;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(antMatcher("/h2-console/**")).permitAll()
                            .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(withDefaults())
                .build();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Autowired
    public void configure(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            DataSource dataSource,
            PasswordEncoder passwordEncoder
    ) throws Exception {

        authenticationManagerBuilder
                .jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository, DataSource dataSource, PasswordEncoder passwordEncoder) {
        final var userDetailsManager = new JdbcUserDetailsManager(dataSource);
        createUser("ivo", memberRepository, passwordEncoder, userDetailsManager);
        createUser("joe", memberRepository, passwordEncoder, userDetailsManager);
        return userDetailsManager;
    }

    protected void createUser(String username, MemberRepository memberRepository, PasswordEncoder passwordEncoder, JdbcUserDetailsManager userDetailsManager) {
        final var user = User.builder().username(username).password(passwordEncoder.encode("admin")).roles("MEMBER").build();
        userDetailsManager.deleteUser(username);
        memberRepository.findByUsername(username).ifPresent(member -> memberRepository.deleteById(member.getId()));
        userDetailsManager.createUser(user);
        memberRepository.save(new Member(username));
    }
}
