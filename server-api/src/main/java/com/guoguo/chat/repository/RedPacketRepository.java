package com.guoguo.chat.repository;

import com.guoguo.chat.entity.RedPacket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedPacketRepository extends JpaRepository<RedPacket,String> {
    //切记JPS select语句查询返回字段,不要写实体对象,要写别名
    @Query("select rpt from RedPacket rpt where rpt.senderId =?1 ")
    Page<RedPacket> findBySenderIdEquals(String senderId, Pageable pageable);
}
