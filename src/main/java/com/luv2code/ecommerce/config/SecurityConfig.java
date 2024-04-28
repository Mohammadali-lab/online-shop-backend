package com.luv2code.ecommerce.config;

import com.luv2code.ecommerce.dao.UserRepository;
import com.luv2code.ecommerce.entity.User;
import com.luv2code.ecommerce.service.UserService;
import com.luv2code.ecommerce.utils.JwtRequestFilter;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
import java.nio.file.attribute.UserPrincipalNotFoundException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class);

    private final UserService userService;
    private final JwtRequestFilter jwtRequestFilter;


    @Lazy
    public SecurityConfig(UserService userService,
                                    JwtRequestFilter jwtRequestFilter) {
        this.userService = userService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        logger.debug(http.toString());
//TODO User Role Permits : "/api/users/check-otp-code" , ,"/whale-bitex/api/users/resend-otp-code"
        http.httpBasic().and()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
//                .antMatchers(HttpMethod.POST,"/api/orders/buy","/api/orders/sell","/hd/transaction-for-withdrawal-request").authenticated()
//                .antMatchers(HttpMethod.DELETE,"/api/orders/**").authenticated()
                .anyRequest().permitAll()
//                .loginPage("/api/users/send-error")
                .and()
                .logout().permitAll()
                .and()
                .exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().headers().frameOptions().sameOrigin()
                .and().csrf().disable();

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        return username -> {
            User user = userRepo.findByUsername(username);
            if (user != null) return user;

            throw new UsernameNotFoundException("User '" + username + "' not found");
        };
    }
}
