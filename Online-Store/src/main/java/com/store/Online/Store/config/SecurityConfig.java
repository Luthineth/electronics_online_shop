package com.store.Online.Store.config;

import com.store.Online.Store.config.jwt.JwtAuthenticationEntryPoint;
import com.store.Online.Store.config.jwt.JwtSecurityConfigurer;
import com.store.Online.Store.config.jwt.JwtTokenUtil;
import com.store.Online.Store.config.jwt.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final JwtTokenUtil jwtTokenUtil;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Autowired
    public SecurityConfig(JwtTokenUtil jwtTokenProvider) {
        this.jwtTokenUtil = jwtTokenProvider;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/authenticate").permitAll()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/comments/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/products/**").permitAll() // Allow GET requests to "/products"
                    .antMatchers(HttpMethod.POST, "/products").authenticated() // Require authentication for POST requests to "/products"
                    .antMatchers(HttpMethod.PUT, "/products").authenticated() // Require authentication for PUT requests to "/products"
                    .antMatchers(HttpMethod.DELETE, "/products/**").authenticated() // Require authentication for DELETE requests to "/products"
                .anyRequest().authenticated()
                .and ()
                .cors(Customizer.withDefaults())
                .apply(new JwtSecurityConfigurer(jwtTokenUtil));
    }
}
