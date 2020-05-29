package com.guoguo.chat.repository;

import com.guoguo.chat.entity.EnvelopesConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnvelopesConfigRepository extends JpaRepository<EnvelopesConfig,Integer> {
    @Query("select ef from EnvelopesConfig ef where ef.type=?1")
    EnvelopesConfig findByType(int type);
}
