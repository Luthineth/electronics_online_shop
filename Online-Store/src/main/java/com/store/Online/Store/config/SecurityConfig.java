package com.store.Online.Store.config;

import com.store.Online.Store.config.jwt.JwtSecurityConfigurer;
import com.store.Online.Store.config.jwt.JwtTokenUtil;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenUtil jwtTokenUtil;
    private final MinioProperties minioProperties;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    @SneakyThrows
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    @Autowired
    public SecurityConfig(JwtTokenUtil jwtTokenProvider, MinioProperties minioProperties) {
        this.jwtTokenUtil = jwtTokenProvider;
        this.minioProperties = minioProperties;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/authenticate","/login").permitAll()

                    .antMatchers(HttpMethod.GET, "/products/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

                    .antMatchers(HttpMethod.GET, "/main").permitAll()
                    .antMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/categories/{categoryId}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/categories/{categoryId}").hasRole("ADMIN")

                    .antMatchers(HttpMethod.GET, "/comments/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/comments").authenticated()
                    .antMatchers(HttpMethod.PUT, "/comments/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/comments/**").hasRole("ADMIN")

                    .antMatchers("/products_category/**").permitAll()

                    .antMatchers("/orders").authenticated()
                .anyRequest().authenticated()
                .and ()
                .cors(Customizer.withDefaults())
                .apply(new JwtSecurityConfigurer(jwtTokenUtil));
    }
}
