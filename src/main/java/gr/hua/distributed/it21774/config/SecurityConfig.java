package gr.hua.distributed.it21774.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean (AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().and().csrf()
                    .disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .antMatchers("/login").permitAll()
                        .antMatchers("/users").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole("ADMIN", "LAWYER", "CLIENT", "NOTARY")
                        .antMatchers(HttpMethod.POST, "/users/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/users/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/users/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/users/{id}/contract").hasAnyRole("LAWYER", "CLIENT")
                        .antMatchers(HttpMethod.POST, "/users/{id}/contract").hasAnyRole("LAWYER", "CLIENT")
                        .antMatchers(HttpMethod.PUT, "/users/{id}/contract").hasAnyRole("LAWYER", "CLIENT")
                        .antMatchers(HttpMethod.GET, "/contracts").hasAnyRole("ADMIN", "NOTARY")
                        .antMatchers(HttpMethod.DELETE, "/contracts/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/contracts/{id}").hasRole("NOTARY")
                        .antMatchers(HttpMethod.PUT, "/contracts/{id}").hasRole("NOTARY")
                        .antMatchers("/contracts/{id}/force-delete").hasRole("ADMIN")
                    .anyRequest()
                        .authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}


