package com.neonark.cli.dto;

import java.time.Instant;

public class ObservationResponse {
    private Long id;
    private String author;
    private String note;
    private Instant createdAt;

    //constructor
    public ObservationResponse() {}

    //getters
    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getNote() {
        return note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    //setters
    public void setId(Long id) {
        this.id = id;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
