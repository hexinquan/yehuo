package com.guoguo.chat.repository;

import com.guoguo.chat.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {

    @Query("select t from Activity t where t.status = ?1 order by t.startTime desc ")
    List<Activity> findAllByStartTimeStatus(int status);
    @Query("select t from Activity t where t.status = ?1 order by t.endTime desc ")
    List<Activity> findAllByEndTimeStatus(int status);
}
