package org.kb.watcher.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;

import org.kb.watcher.Repository.UserRepository;
import org.kb.watcher.dto.User;
import org.kb.watcher.helper.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class AppService {

	@Autowired
	MailSendingToClint clint;

	@Autowired
	UserRepository repository;

	public String validateUser(User user, BindingResult result, HttpSession session) {
		if (!user.getPassword().equals(user.getConfirmpassword())) {
			result.rejectValue("confirmpassword", "error.confirmpassword", "Password mismatch");
		}

		if (repository.existsByEmail(user.getEmail())) {
			result.rejectValue("email", "error.email", "Email already exists");
		}

		if (repository.existsByMobile(user.getMobile())) {
			result.rejectValue("mobile", "error.mobile", "Mobile number already taken");
		}

		if (repository.existsByUsername(user.getUsername())) {
			result.rejectValue("username", "error.username", "Username already taken");
		}

		if (result.hasErrors()) {
			System.err.println(user.getEmail());
			return "register.html";
		} else {
			user.setPassword(AES.encrypt(user.getPassword()));
			int otp = new Random().nextInt(100000, 1000000);
			System.err.println(otp);
			user.setOtp(otp);
			repository.save(user);
			session.setAttribute("pass", "OTP sent Sucessfully");

//			try {
//				clint.send(user.getEmail(), user.getOtp(), user.getFirstname());
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			} catch (MessagingException e) {
//				e.printStackTrace();
//			}

			return "redirect:/verify-otp/" + user.getId();
		}
	}

	public String verifyOtp(int otp, int id, HttpSession session) {
		User user = repository.findById(id).get();
		if ((user.getOtp() == otp) && (user.getId() == id)) {
			user.setVerified(true);
			user.setOtp(0);
			repository.save(user);
			session.setAttribute("pass", "Account created Sucessfully");
			return "redirect:/login";
		} else {
			session.setAttribute("fail", "Invalid OTP, please try again");
			return "redirect:/verify-otp/" + id;
		}
	}

	public String resendOtp(int id, HttpSession session) {
		User user = repository.findById(id).get();
		int otp = new Random().nextInt(100000, 1000000);
		System.err.println(otp);
		user.setOtp(otp);
		repository.save(user);
		session.setAttribute("pass", "OTP sent Sucessfully");

//		try {
//			clint.send(user.getEmail(), user.getOtp(), user.getFirstname());
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}

		return "redirect:/verify-otp/" + user.getId();
	}

	public String login(String username, String password, HttpSession session) {
		User user = repository.findByUsername(username);
		if (user == null) {
			session.setAttribute("fail", "Invalid Username");
			return "redirect:/login";
		} else {
			if (AES.decrypt(user.getPassword()).equals(password)) {
				if (user.isVerified()) {
					session.setAttribute("user", user);
					session.setAttribute("pass", "Login Sucessful");
					user.setInorout(true);
					repository.save(user);
					return "redirect:/home";
				} else {
					int otp = new Random().nextInt(100000, 1000000);
					System.err.println(otp);
					user.setOtp(otp);
					repository.save(user);
					session.setAttribute("pass", "OTP sent Sucessfully");

//					try {
//						clint.send(user.getEmail(), user.getOtp(), user.getFirstname());
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					} catch (MessagingException e) {
//						e.printStackTrace();
//					}

					return "redirect:/verify-otp/" + user.getId();
				}
			} else {
				session.setAttribute("fail", "Incorrect Password");
				return "redirect:/login";
			}
		}
	}

	public String homeLoad(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			return "home.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String logout(HttpSession session) {
		User user=(User) session.getAttribute("user");
		user.setInorout(false); 
		repository.save(user);
		session.removeAttribute("user");
		session.setAttribute("pass", "logout Sucessfull");
		return "redirect:/login";
	}

	public String profilePage(String username, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			System.err.println(username);
			return "profile.html";
		} else {
			session.setAttribute("fail", "Session Timeout");
			return "redirect:/login";
		}

	}

}
