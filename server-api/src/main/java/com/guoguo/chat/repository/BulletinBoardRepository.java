package com.guoguo.chat.repository;

import com.guoguo.chat.entity.AccountRecord;
import com.guoguo.chat.entity.BulletinBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulletinBoardRepository extends JpaRepository<BulletinBoard,Integer> {
}
