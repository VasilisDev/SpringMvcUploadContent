package com.bill.uploadManagment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {



    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                 .withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/login/*").permitAll()
                .antMatchers("/logout").permitAll()
                .antMatchers("/session*").permitAll()
                .antMatchers("/errors/**").permitAll()
                .antMatchers("/admin/*").hasRole("ADMIN")
                .antMatchers("/**").hasAnyRole("ANONYMOUS","ADMIN")

                .and()
                .exceptionHandling().accessDeniedPage("/errors/403")

                .and()
                .formLogin()
                .loginPage("/login/form")
                .loginProcessingUrl("/login")
                .failureUrl("/login/form?error")
                .defaultSuccessUrl("/default", true)
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()

                .and().sessionManagement()
                        .sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredUrl("/session-expired")
                        .and()
                        .enableSessionUrlRewriting(true)
                        .invalidSessionUrl("/session-invalid")

                 .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login/form?logout")
                .permitAll()
                .and().logout().deleteCookies("JSESSIONID")
                .and().logout().invalidateHttpSession(true)
                .and().rememberMe().key("remember-me").rememberMeParameter("remember-me").rememberMeCookieName("rememberme").tokenValiditySeconds(100)
                .and().httpBasic()
                .and().anonymous()
                // CSRF is enabled by default, with Java Config
                .and().csrf().disable()

        ;
    }
    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/resources/**");
    }

    /**
     *   Not applicable in this app.If you want to customize userDetails extend the Authentication manager.
     */
    @Bean
    @Override
    public UserDetailsManager userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin").password("admin").roles("ADMIN").build());
        return manager;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()
            throws Exception {
        return super.authenticationManagerBean();
    }
}
