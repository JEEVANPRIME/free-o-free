package org.kb.watcher.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String fileurl;
	private String caption;
	@CreationTimestamp
	private Date postTime;

	@Transient
	private MultipartFile image;

	@ManyToOne
	private User user;

	@ManyToMany
	List<User> likedUsers = new ArrayList<User>();

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	List<Comment> comments = new ArrayList<Comment>(); 

	public boolean hasLiked(int id) {
		for (User likedUser : likedUsers) {
			if (likedUser.getId() == id) {
				return true;
			}
		}
		return false;
	}

}
