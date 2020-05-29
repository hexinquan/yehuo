package com.guoguo.chat.repository;

import com.guoguo.chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    @Modifying
    @Query("update User u set u.balanceAmount=u.balanceAmount+?1 where u.uid=?2")
   int incBalanceAmountByUserId(Integer balanceAmount,String id);

    @Query("select u from User u where u.uid=?1")
    User findByUid(String uid);

}
