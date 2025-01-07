package org.kb.watcher.AppController;

import org.kb.watcher.dto.User;
import org.kb.watcher.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AppController {

	@Autowired
	AppService service;

	@GetMapping({ "/", "/login" })
	public String homePage(ModelMap map, User user) {
		map.put("user", user);
		return "login.html";
	}

	@GetMapping("/register")
	public String registerPage(ModelMap map, User user) {
		map.put("user", user);
		return "register.html";
	}

	@PostMapping("/register")
	public String registerLoad(@Valid User user, BindingResult result, HttpSession session) {
		return service.validateUser(user, result, session);
	}

	@GetMapping("/verify-otp/{id}")
	public String otpPage(@PathVariable int id, ModelMap map) {
		map.put("id", id);
		return "otp.html";
	}

	@PostMapping("/verify-otp")
	public String otpVerify(@RequestParam int otp, @RequestParam int id, HttpSession session) {
		return service.verifyOtp(otp, id, session);
	}

	@GetMapping("/resend-otp/{id}")
	public String resendOtp(@PathVariable int id, HttpSession session) {
		return service.resendOtp(id, session);
	}

	@PostMapping("/login")
	public String loginLoad(@RequestParam String username, @RequestParam String password, HttpSession session) {
		return service.login(username, password, session);
	}

	@GetMapping("/home")
	public String homePage(HttpSession session) {
		return service.homeLoad(session);
	}

	@GetMapping("/profile/{username}")
	public String profilePage(@PathVariable String username, HttpSession session) {
		return service.profilePage(username, session); 
	}

	@GetMapping("/edit-profile/{username}")
	public String editProfile(@PathVariable String username, HttpSession session) {
		return service.editProfile(username, session); 
	}

	@PostMapping("/update-profile")
	public String editProfile(HttpSession session, @RequestParam MultipartFile image, @RequestParam String bio) {
		return service.updateProfile(session, image, bio);
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return service.logout(session);
	}

}
