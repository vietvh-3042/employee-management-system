package com.learning.employeemanagement.controller;

import com.learning.employeemanagement.constant.SecurityConstants;
import com.learning.employeemanagement.constant.WebPaths;
import com.learning.employeemanagement.dto.AuthResponse;
import com.learning.employeemanagement.dto.LoginRequest;
import com.learning.employeemanagement.dto.RegisterRequest;
import com.learning.employeemanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class AuthViewController {

    private final AuthService authService;
    private final boolean secureCookie;

    public AuthViewController(
            AuthService authService,
            @Value("${app.security.cookie-secure:false}") boolean secureCookie) {
        this.authService = authService;
        this.secureCookie = secureCookie;
    }

    @GetMapping(WebPaths.LOGIN)
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest("", ""));
        return "auth/login";
    }

    @PostMapping(WebPaths.LOGIN)
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequest request,
            BindingResult bindingResult,
            Model model,
            jakarta.servlet.http.HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        try {
            setAuthCookie(response, authService.login(request));
            return "redirect:" + WebPaths.EMPLOYEE_LIST;
        } catch (ResponseStatusException exception) {
            model.addAttribute("error", exception.getReason());
            return "auth/login";
        }
    }

    @GetMapping(WebPaths.REGISTER)
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest("", ""));
        return "auth/register";
    }

    @PostMapping(WebPaths.REGISTER)
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            Model model,
            jakarta.servlet.http.HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            setAuthCookie(response, authService.register(request));
            return "redirect:" + WebPaths.EMPLOYEE_LIST;
        } catch (ResponseStatusException exception) {
            model.addAttribute("error", exception.getReason());
            return "auth/register";
        }
    }

    @PostMapping(WebPaths.LOGOUT)
    public String logout(jakarta.servlet.http.HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(SecurityConstants.AUTH_COOKIE, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build()
                .toString());
        return "redirect:" + WebPaths.LOGIN;
    }

    private void setAuthCookie(jakarta.servlet.http.HttpServletResponse response, AuthResponse auth) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(SecurityConstants.AUTH_COOKIE, auth.token())
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/")
                .build()
                .toString());
    }
}
