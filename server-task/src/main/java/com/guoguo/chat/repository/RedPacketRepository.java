package com.guoguo.chat.repository;

import com.guoguo.chat.entity.RedPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedPacketRepository extends JpaRepository<RedPacket,String> {
    @Query("select rpt from RedPacket rpt where rpt.senderId =?1 ")
    List<RedPacket> findBySenderIdEquals(String senderId);

    @Query("select rpt from RedPacket rpt where rpt.redStatus=1")
    List<RedPacket> findByOutTimeEquals();
}
