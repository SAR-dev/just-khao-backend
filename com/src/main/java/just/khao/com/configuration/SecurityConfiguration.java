package just.khao.com.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and();
        http.csrf().disable().authorizeHttpRequests()
                .antMatchers("/v1/sign-in").permitAll()
                .antMatchers("/v1/sign-up").permitAll()
                .antMatchers("/v1/refreshToken").permitAll()
                .antMatchers("/v1/user").permitAll()
                .antMatchers("/**").authenticated()
                .anyRequest()
                .authenticated();
    }
}
