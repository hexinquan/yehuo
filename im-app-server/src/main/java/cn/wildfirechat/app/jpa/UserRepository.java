package cn.wildfirechat.app.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<User,Integer> {
    @Query("select u from User u where u.uid =?1")
    User findByUid(String uid);
}
