package org.crmApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
public class SecurityConfig  {


       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                   .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity (customize as needed)
                   .authorizeHttpRequests(auth -> auth
                           /*.requestMatchers("/admin/**").hasRole("ADMIN")
                           .requestMatchers("/user/**").hasRole("USER")*/
                           .requestMatchers("/h2-console/**").permitAll()
                           .anyRequest().authenticated()
                   )
                   .oauth2Login(withDefaults()); // Use the new Customizer approach for OAuth2 login

           return http.build();
       }


  @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

}
