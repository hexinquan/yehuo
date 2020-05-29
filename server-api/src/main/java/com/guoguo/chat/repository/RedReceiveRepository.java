package com.guoguo.chat.repository;

import com.guoguo.chat.entity.RedReceive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedReceiveRepository extends JpaRepository<RedReceive,Long> {
    @Query("select rr from RedReceive rr where rr.redId =?1 and rr.receiveId = ?2")
    RedReceive findByRedIdAndUserId(String redId, String receiveId);

    @Query("select rr from RedReceive rr where rr.redId =?1")
    List<RedReceive> findByRedId(String redId);

    @Query("select rr from RedReceive rr where rr.receiveId =?1")
    Page<RedReceive> findByReceiveId(String uuid, Pageable pageable);
}
