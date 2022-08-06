package cn.har01d.notebook.core.config

import cn.spark2fire.auth.token.TokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.bind.annotation.CrossOrigin
import javax.servlet.http.HttpServletResponse

@Configuration
@CrossOrigin
@EnableWebSecurity
class WebSecurityConfiguration(private val tokenFilter: TokenFilter) : WebSecurityConfigurerAdapter() {
    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint? {
        return AuthenticationEntryPoint { _, response, _ ->
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
        }
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy? {
        val roleHierarchyImpl = RoleHierarchyImpl()
        roleHierarchyImpl.setHierarchy("ROLE_ADMIN > ROLE_STAFF > ROLE_USER")
        return roleHierarchyImpl
    }

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers(HttpMethod.GET, "/config/**").permitAll()
            .antMatchers(HttpMethod.GET, "/menus").permitAll()
            .antMatchers(HttpMethod.POST, "/accounts/login", "/accounts/logout", "/accounts/signup", "/accounts/heartbeat").permitAll()
            .antMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/tags/**").hasRole("ADMIN")
            .antMatchers("/menus/**").hasRole("ADMIN")
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/config/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST).authenticated()
            .antMatchers(HttpMethod.PUT).authenticated()
            .antMatchers(HttpMethod.PATCH).authenticated()
            .antMatchers(HttpMethod.DELETE).authenticated()
            .anyRequest().permitAll()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf().disable()
            .formLogin().disable()
            .logout().disable()
            .addFilterBefore(tokenFilter, BasicAuthenticationFilter::class.java)
    }
}
