package org.kb.watcher.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.kb.watcher.Repository.PostRepository;
import org.kb.watcher.Repository.UserRepository;
import org.kb.watcher.dto.Post;
import org.kb.watcher.dto.User;
import org.kb.watcher.helper.AES;
import org.kb.watcher.helper.CloudinaryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class AppService {

	@Autowired
	MailSendingToClint clint;

	@Autowired
	UserRepository repository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	CloudinaryHelper helper;

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

	public String homeLoad(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			List<User> users = user.getFollowing();
			List<Post> posts = postRepository.findByUserIn(users);
			if (!posts.isEmpty())
				map.put("posts", posts);
			return "home.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String logout(HttpSession session) {
		User user = (User) session.getAttribute("user");
		user.setInorout(false);
		repository.save(user);
		session.removeAttribute("user");
		session.setAttribute("pass", "logout Sucessfull");
		return "redirect:/login";
	}

	public String profilePage(String username, HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			List<Post> posts = postRepository.findByUser(user);
			if (!posts.isEmpty())
				map.put("posts", posts);

			System.err.println(username);
			return "profile.html";
		} else {
			session.setAttribute("fail", "Session Timeout");
			return "redirect:/login";
		}

	}

	public String editProfile(String username, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			return "edit-profile.html";
		} else {
			session.setAttribute("fail", "Session Timedout");
			return "redirect:/login";
		}
	}

	public String updateProfile(HttpSession session, MultipartFile image, String bio) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			user.setBio(bio);
			user.setImageurl(helper.saveImage(image));
			repository.save(user);
			return "redirect:/profile/" + user.getUsername();
		} else {
			return "redirect:/login";
		}
	}

	public String addPost(String username, HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			List<Post> posts = postRepository.findByUser(user);
			if (!posts.isEmpty())
				map.put("posts", posts);

			return "add-post.html";
		} else {
			session.setAttribute("fail", "Session Timeout");
			return "redirect:/login";
		}
	}

	public String uploadPost(Post post, MultipartFile file, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			post.setFileurl(helper.saveUploadImage(file));
			post.setCaption(post.getCaption());
			post.setUser(user);
			postRepository.save(post);
			session.setAttribute("pass", "Posted Sucessfully");
			return "redirect:/profile/" + user.getUsername();
		} else {
			session.setAttribute("fail", "Invalid session");
			return "redirect:/login";
		}
	}

	public String deletePost(HttpSession session, int id) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			postRepository.deleteById(id);
			session.setAttribute("pass", "Post deleted sucessfully");
			return "redirect:/profile/" + user.getUsername();
		} else {
			session.setAttribute("fail", "Invalid session");
			return "redirect:/login";
		}
	}

	public String editPost(int id, HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			Post posts = postRepository.findById(id).get();
			map.put("post", posts);
			return "edit-post.html";
		} else {
			session.setAttribute("fail", "Session Timeout");
			return "redirect:/login";
		}
	}

	public String updatePost(Post post, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			if (!post.getImage().isEmpty())
				post.setFileurl(helper.saveImage(post.getImage()));
			else
				post.setFileurl(postRepository.findById(post.getId()).get().getFileurl());
			post.setUser(user);
			postRepository.save(post);
			session.setAttribute("pass", "Updated Success");
			return "redirect:/profile/" + user.getUsername();
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String suggestUser(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			List<User> suggestions = repository.findByVerifiedTrue();
			List<User> usersToRemove = new ArrayList<User>();

			for (User suggestion : suggestions) {
				if (suggestion.getId() == user.getId()) {
					usersToRemove.add(suggestion);
				}
				for (User followingUser : user.getFollowing()) {
					if (followingUser.getId() == suggestion.getId()) {
						usersToRemove.add(suggestion);
					}
				}
			}
			if (suggestions.isEmpty()) {
				session.setAttribute("fail", "No suggestion");
				return "redirect:/profile/" + user.getUsername();
			} else {
				suggestions.removeAll(usersToRemove);
				map.put("suggestions", suggestions);
				return "suggestions.html";
			}
		} else {
			session.setAttribute("fail", "Session Timedout");
			return "redirect:/login";
		}
	}

	public String followers(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			User followedUser = repository.findById(id).get();
			user.getFollowing().add(followedUser);
			followedUser.getFollowers().add(user);
			repository.save(user);
			repository.save(followedUser);
			session.setAttribute("user", repository.findById(user.getId()).get());
			return "redirect:/profile/" + user.getUsername();
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String forgotPassword(ModelMap map) {
		return "forgot-password.html";
	}

	public String sendEmail(String email, HttpSession session, ModelMap map) {

		User user = repository.findByEmail(email);
		if (user == null) {
			session.setAttribute("fail", "Email does not exists");
			return "redirect:/login";
		}
		if (user.getEmail().equals(email)) {
			System.err.println(email);
			map.put("showOtpForm", user);
			int otp = new Random().nextInt(100000, 1000000);
			user.setOtp(otp);
			repository.save(user);
			System.err.println(otp);
			session.setAttribute("pass", "Email is valid");
//		try {
//			clint.send(user.getEmail(), user.getOtp(), user.getFirstname());
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}
			return "forgot-password.html";
		} else {
			session.setAttribute("fail", "Email is not present");
			return "redirect:/login";
		}
	}

	public String confirmPassword(@Valid User user, BindingResult result, ModelMap map, HttpSession session) {
		User userID = repository.findById(user.getId()).get();
		if (user.getOtp() == userID.getOtp() && user.getId() == userID.getId()) {
			if (!user.getPassword().equals(user.getConfirmpassword())) {
				session.setAttribute("fail", "Password Mismatch");
				return "forgot-password.html";
			} else {
				userID.setPassword(AES.encrypt(user.getPassword()));
				userID.setOtp(0);
				repository.save(userID);
				session.setAttribute("pass", "Password changed sucessfully");
				return "redirect:/login";
			}
		} else {
			map.put("showOtpForm", userID);
			map.put("user", userID);
			session.setAttribute("fail", "OTP mismatch");
			return "forgot-password.html";
		}
	}

	public String followingList(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			List<User> following = user.getFollowing();
			if (following.isEmpty()) {
				session.setAttribute("fail", "Not following anyone");
				return "redirect:/profile/" + user.getUsername();
			} else {
				map.put("followings", following);
				return "following.html";
			}

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String followersList(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			List<User> followers = user.getFollowers();
			if (followers.isEmpty()) {
				session.setAttribute("fail", "No followers");
				return "redirect:/profile/" + user.getUsername();
			} else {
				map.put("followers", followers);
				return "followers.html";
			}

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String unFollow(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			User unFollowUser = null;
			for (User unFollow : user.getFollowing()) {
				if (id == unFollow.getId()) {
					unFollowUser = unFollow;
					break;
				}
			}
			user.getFollowing().remove(unFollowUser);
			repository.save(user);

			User unUser = null;
			for (User unUserFollow : unFollowUser.getFollowers()) {
				if (user.getId() == unUserFollow.getId()) {
					unUser = unUserFollow;
					break;
				}
			}
			unFollowUser.getFollowers().remove(unUser);
			repository.save(unFollowUser);
			session.setAttribute("user", repository.findById(user.getId()).get());
			return "redirect:/profile/" + user.getUsername();
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String viewProfile(int id, HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			User checkedUser = repository.findById(id).get();
			List<Post> posts = postRepository.findByUser(checkedUser);
			if (!posts.isEmpty())
				map.put("posts", posts);
			map.put("user", checkedUser);
			return "view-profile.html";

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String likeUser(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			Post post = postRepository.findById(id).get();
			boolean flag = true;
			for (User likedUser : post.getLikedUsers()) {
				if (likedUser.getId() == user.getId()) {
					flag = false;
					break;
				}
			}
			if (flag) {
				post.getLikedUsers().add(user);
			}
			postRepository.save(post);
			return "redirect:/home";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String dislikeUser(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null && user.isInorout()) {
			Post post = postRepository.findById(id).get();
			for (User likedUser : post.getLikedUsers()) {
				if (likedUser.getId() == user.getId()) {
					post.getLikedUsers().remove(likedUser);
					break;
				}
			}
			postRepository.save(post);
			return "redirect:/home";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

}
