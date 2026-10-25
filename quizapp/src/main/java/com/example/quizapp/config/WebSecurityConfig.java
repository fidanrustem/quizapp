package com.example.quizapp.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure endpoint authorization requirements
                .authorizeHttpRequests(auth -> auth
                        // Anyone can access registration and login
                        .requestMatchers("/login", "/register").permitAll()

                        // Only users with ADMIN role can access QuizList
                        .requestMatchers("/quizList", "/quizList/**").hasRole("ADMIN")

                        // Only users with USER role can access Quiz
                        .requestMatchers("/quiz", "/quiz/**").hasRole("USER")

                        // All other endpoints require the user to be logged in
                        .anyRequest().authenticated()
                )
                // Configure the custom login page
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/") // Redirect after successful login
                        .permitAll()
                )
                // Configure logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Şifrələməni ləğv edirik ki, HashMap-dəki açıq parollarla giriş etmək mümkün olsun
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }
}