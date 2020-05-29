package com.guoguo.chat.repository;

import com.guoguo.chat.entity.Extension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtensionRepository extends JpaRepository<Extension,String> {

}
