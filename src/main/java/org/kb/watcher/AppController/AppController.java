package org.kb.watcher.AppController;

import java.io.IOException;

import org.kb.watcher.dto.Post;
import org.kb.watcher.dto.User;
import org.kb.watcher.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	public String homePage(HttpSession session, ModelMap map) {
		return service.homeLoad(session, map);
	}

	@GetMapping("/profile/{username}")
	public String profilePage(@PathVariable String username, HttpSession session, ModelMap map) {
		return service.profilePage(username, session, map);
	}

	@GetMapping("/edit-profile/{username}")
	public String editProfile(@PathVariable String username, HttpSession session) {
		return service.editProfile(username, session);
	}

	@PostMapping("/update-profile")
	public String editProfile(HttpSession session, @RequestParam MultipartFile image, @RequestParam String bio) {
		return service.updateProfile(session, image, bio);
	}

	@GetMapping("/add-post/{username}")
	public String addPost(@PathVariable String username, HttpSession session, ModelMap map) {
		return service.addPost(username, session, map);
	}

	@PostMapping("/upload")
	public String uploadPost(Post post, @RequestParam MultipartFile file, HttpSession session) {
		return service.uploadPost(post, file, session);
	}

	@GetMapping("/edit-post/{id}")
	public String editPost(@PathVariable int id, HttpSession session, ModelMap map) {
		return service.editPost(id, session, map);
	}

	@PostMapping("/edit-post")
	public String editPost(Post post, HttpSession session) {
		return service.updatePost(post, session);
	}

	@GetMapping("/delete-post/{id}")
	public String deletePost(HttpSession session, @PathVariable int id) {
		return service.deletePost(session, id);
	}

	@GetMapping("/suggestions")
	public String suggestUsers(HttpSession session, ModelMap map) {
		return service.suggestUser(session, map);
	}

	@GetMapping("/follow/{id}")
	public String followers(@PathVariable int id, HttpSession session) {
		return service.followers(id, session);
	}

	@GetMapping("/unfollow/{id}")
	public String unFollow(@PathVariable int id, HttpSession session) {
		return service.unFollow(id, session);
	}

	@GetMapping("/forgot-password")
	public String forgotPassword(ModelMap map) {
		return service.forgotPassword(map);
	}

	@PostMapping("/sendOtp")
	public String verifyEmail(@RequestParam String email, HttpSession session, ModelMap map) {
		return service.sendEmail(email, session, map);
	}

	@PostMapping("/resetPassword")
	public String confirmPassword(@Valid User user, BindingResult result, ModelMap map, HttpSession session) {
		return service.confirmPassword(user, result, map, session);
	}

	@GetMapping("/following/{username}")
	public String followingList(HttpSession session, ModelMap map) {
		return service.followingList(session, map);
	}

	@GetMapping("/followers/{username}")
	public String followersList(HttpSession session, ModelMap map) {
		return service.followersList(session, map);
	}

	@GetMapping("/view-profile/{id}")
	public String viewProfile(@PathVariable int id, HttpSession session, ModelMap map) {
		return service.viewProfile(id, session, map);
	}

	@GetMapping("/like/{id}")
	public String likeUser(@PathVariable int id, HttpSession session) {
		return service.likeUser(id, session); 
	}
	
	@GetMapping("/dislike/{id}")
	public String dislikeUser(@PathVariable int id, HttpSession session) {
		return service.dislikeUser(id, session); 
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return service.logout(session);
	}

}
