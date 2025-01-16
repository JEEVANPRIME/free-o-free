package org.kb.watcher.Repository;

import java.util.List;

import org.kb.watcher.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

	boolean existsByMobile(long mobile);

	boolean existsByUsername(String username);

	User findByUsername(String username);

	List<User> findByVerifiedTrue();
 
	User findByEmail(String email);

	List<User> findByFollowing(User user);


}
