package org.com.controller.web;

import jakarta.validation.Valid;
import org.com.domain.dto.UserRegistrationDTO;
import org.com.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
        Web-контроллер аутентификации (Thymeleaf)
        Обслуживает HTML-страницы /login и /register
        Отдаёт представления login.html и register.html, обрабатывает форму регистрации
        Для программной JSON аутентификации см. AuthRestController
 */
@Controller
public class WebAuthController {

    private final UserService userService;

    public WebAuthController(UserService userService) {
        this.userService = userService;
    }

    // Web-контроллер для страниц логина и регистрации
    // отдаёт HTML-страницы login.html и register.html, обрабатывает отправку формы регистрации, делает редиректы
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("ERROR_Message", "Неверный адрес электронной почты или пароль");
        }
        if (logout != null) {
            model.addAttribute("INFO_Message", "Вы успешно вышли из системы");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        model.addAttribute("userDTO", new UserRegistrationDTO());
        return "register";
    }

    /**
        Обрабатывает отправку формы регистрации
        Если Bean Validation нашла ошибки - возвращает на ту же страницу без редиректа
        Если сервис выбросил исключение (дубликат email/телефона) - кладёт сообщение в модель и снова показывает форму, не делая редирект
        При успехе редирект на /login с flash-сообщением
     */
    @GetMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("userDTO") UserRegistrationDTO userRegistrationDTO,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(userRegistrationDTO);
            redirectAttributes.addAttribute("SUCCESS_Message", "Регистрация прошла успешно! Теперь вы можете войти.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("ERROR_Message", e.getMessage());
            return "register";
        }
    }
}
