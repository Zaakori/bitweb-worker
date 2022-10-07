package com.adelchik.Worker.db.repository;

import com.adelchik.Worker.db.entities.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface TextRepository extends JpaRepository<TextEntity, Long> {


    @Transactional
    @Modifying
    @Query(value = "UPDATE text SET status = ?1 WHERE id = ?2", nativeQuery = true)
    void updateStatus(String status, String entityId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE text SET processedtext = ?1 WHERE id = ?2", nativeQuery = true)
    void updateProcessedText(String processedText, String entityId);

    @Transactional
    @Query(value = "SELECT * FROM text WHERE id = ?1", nativeQuery = true)
    TextEntity findByStringId(String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE text SET processed_chunk_amount = ?1 WHERE id = ?2", nativeQuery = true)
    void updateProcessedChunkAmount(int processedAmount, String entityId);


}
