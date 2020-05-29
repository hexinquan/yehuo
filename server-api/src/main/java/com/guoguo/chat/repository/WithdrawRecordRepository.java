package com.guoguo.chat.repository;

import com.guoguo.chat.entity.WithdrawRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawRecordRepository  extends JpaRepository<WithdrawRecord,String> {
    @Query("select wr from WithdrawRecord wr where wr.uid=?1")
    Page<WithdrawRecord> findByUid(String uid, Pageable pageable);
}
