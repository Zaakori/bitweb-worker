package com.adelchik.Worker.db.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TEXT")
public class TextEntity {

    @Id
    private String id;
    private String status;
    private int total_chunk_amount;
    private int processed_chunk_amount;
    private String processedtext;

    public TextEntity() {
    }

    public TextEntity(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotal_chunk_amount() {
        return total_chunk_amount;
    }

    public void setTotal_chunk_amount(int total_chunk_amount) {
        this.total_chunk_amount = total_chunk_amount;
    }

    public int getProcessed_chunk_amount() {
        return processed_chunk_amount;
    }

    public void setProcessed_chunk_amount(int processed_chunk_amount) {
        this.processed_chunk_amount = processed_chunk_amount;
    }

    public String getProcessedtext() {
        return processedtext;
    }

    public void setProcessedtext(String processedtext) {
        this.processedtext = processedtext;
    }
}