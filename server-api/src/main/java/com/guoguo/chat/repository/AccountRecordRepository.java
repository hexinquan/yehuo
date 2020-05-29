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
    @Query("select ac from AccountRecord ac where ac.uid=?1 and ac.recordType=?2")
    Page<AccountRecord> findByUidAndRecordType(String uid, int type,Pageable pageable);
    @Query("select ac from AccountRecord ac where ac.uid=?1 and ac.recordType=?2 and ac.flow=?3")
    Page<AccountRecord> findByUidAndRecordTypeAndFlow(String uid, int type,int flow,Pageable pageable);
    @Query("select ac from AccountRecord ac where ac.uid=?1 and ac.flow=?2")
    Page<AccountRecord> findByUidAndFlow(String uid,int flow,Pageable pageable);
    @Query("select ac from AccountRecord ac where ac.recordTypeId=?1")
    AccountRecord findByWithdrawId(String id);
}
