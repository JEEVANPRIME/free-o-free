package org.kb.watcher.AppController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping({ "/", "/login" })
    public String homePage() {
        return "login.html";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register.html";
    }

    @GetMapping("/verify-otp")
    public String otpPage() {
        return "otp.html";
    }

}
