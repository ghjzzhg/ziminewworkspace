package org.activiti.rest.conf;

import org.activiti.rest.security.BasicAuthenticationProvider;
import org.activiti.rest.servlet.CustomAuthenticationProvider;
import org.activiti.rest.servlet.CustomTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /*@Bean
    public AuthenticationProvider authenticationProvider() {
        return new BasicAuthenticationProvider();
    }*/

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
//        System.out.println("+++ create new CustomTokenAuthenticationFilter for path=/**");
        return new CustomAuthenticationProvider();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        // The order is important! During runtime Spring Security tries to find Provider-Implementations that
        // match the UsernamePasswordAuthenticationToken (which will be created later..). We must make sure
        // that daoAuthenticationProvider matches first. Why? Hard to explain, I figured it out with the debugger.
//        auth.authenticationProvider(daoAuthenticationProvider());
        auth.authenticationProvider(customAuthenticationProvider());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .authenticationProvider(authenticationProvider())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable()
                .authorizeRequests().antMatchers("/service").hasRole("USER").and()
                .addFilterBefore(new CustomTokenAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class).httpBasic()
                /*.and().authorizeRequests().antMatchers("*//**").permitAll().anyRequest().authenticated()*/;
    }
}
