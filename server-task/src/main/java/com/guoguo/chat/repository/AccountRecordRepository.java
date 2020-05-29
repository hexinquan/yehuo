package com.guoguo.chat.repository;

import com.guoguo.chat.entity.AccountRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRecordRepository  extends JpaRepository<AccountRecord,String> {
    @Query("select ac from AccountRecord ac where ac.uid=?1")
    Page<AccountRecord> findByUid(String uid, Pageable pageable);

}
