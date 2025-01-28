package org.kb.watcher.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Size(min = 3, max = 10, message = "It should be between 3 and 10")
	private String firstname;
	@Size(min = 1, max = 15, message = "It should be between 1 and 15")
	private String lastname;
	@Size(min = 5, max = 15, message = "It should be between 5 and 15")
	private String username;
	@Email(message = "Give proper mail format")
	@NotEmpty(message = "Should not be empty")
	private String email;
	@DecimalMin(value = "6000000000", message = "Give proper number format")
	@DecimalMax(value = "9999999999", message = "Give proper number format")
	private long mobile;
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
	private String password;
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
	@Transient
	private String confirmpassword;
	@NotNull(message = "It should not be empty")
	private String gender;
	private int otp;
	private boolean verified;
	private boolean inorout;
	private String bio;
	private String imageurl;
	private boolean prime;

	@ManyToMany(fetch = FetchType.EAGER)
	List<User> followers = new ArrayList<User>();

	@ManyToMany(fetch = FetchType.EAGER)
	List<User> following = new ArrayList<User>();

	public boolean isFollowing() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session = attributes.getRequest().getSession();
		User user = (User) session.getAttribute("user");
		if (user != null) {
			for (User user2 : user.following) {
				if (this.id == user2.id) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

}
