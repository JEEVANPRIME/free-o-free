package org.kb.watcher.Repository;

import java.util.List;

import org.kb.watcher.dto.Post;
import org.kb.watcher.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {


	List<Post> findByUser(User user);

	List<Post> findByUserIn(List<User> users); 

}
