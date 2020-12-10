package cn.har01d.notebook.core.config

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
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
class WebSecurityConfiguration : WebSecurityConfigurerAdapter(), WebMvcConfigurer {
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
                .antMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable();
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").allowedMethods("*")
    }
}
