package com.eunsil.guestbook.repository;

import com.eunsil.guestbook.domain.entity.Card;
import com.eunsil.guestbook.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(value = "SELECT * FROM card WHERE id = :id", nativeQuery = true)
    Card findById(String id);

    @Query(value = "SELECT * FROM card WHERE content LIKE %:content%", nativeQuery = true)
    List<Card> findAllByContent(String content);

    @Query(value = "SELECT * FROM card WHERE user_id = :user AND content LIKE %:content%", nativeQuery = true)
    List<Card> findAllByUserByContentOrderByIdDesc(String content, User user);

    List<Card> findAllByUserOrderByIdDesc(User user);

    List<Card> findAllByOrderByIdDesc(Pageable pageable);

}
