package com.admin.config;

import com.admin.common.bean.ResultBean;
import com.admin.filter.JwtAuthenticationTokenFilter;
import com.admin.service.AdminUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] STATIC_RES = new String[]{"/*.html", "/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js"};
    private static final String[] SKIP_AUTH = new String[]{"/user/login", "/admin/user/login", "/test/**", "/v3/**", "/api/**"};

    private AdminUserService adminUserService;
    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    public void setAdminUserService(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(adminUserService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ??????????????????JWT????????????????????????csrf
        http.csrf().disable();
        // ??????token??????????????????session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                // ????????????????????????????????????????????????
                .antMatchers(HttpMethod.GET, STATIC_RES)
                .permitAll()
                // ????????????????????????????????????
                .antMatchers(SKIP_AUTH)
                .permitAll()
                //??????????????????????????????options??????
                .antMatchers(HttpMethod.OPTIONS)
                .permitAll()
                // ???????????????????????????????????????????????????
                .anyRequest()
                .authenticated();
        // ????????????
        http.headers().cacheControl();
        // ??????JWT filter
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // ?????????????????????????????????
        http.exceptionHandling()
                .accessDeniedHandler((request, response, e) -> restJson(response, "????????????????????????", e.getLocalizedMessage()))
                .authenticationEntryPoint((request, resp, e) -> restJson(resp, "??????????????????", e.getLocalizedMessage()));
    }

    private static void restJson(HttpServletResponse resp,
                                 String msg,
                                 String errorMsg) throws IOException {
        log.error("Spring Security ??????: {},errorMsg=>{}", msg, errorMsg);
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        ResultBean<String> resultBean = new ResultBean<>();
        resultBean.setCode(401);
        resultBean.setMsg(msg);
        writer.write(new ObjectMapper().writeValueAsString(resultBean));
        writer.flush();
        writer.close();
    }
}