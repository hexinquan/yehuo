package com.guoguo.chat.repository;

import com.guoguo.chat.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version,Integer> {
}
