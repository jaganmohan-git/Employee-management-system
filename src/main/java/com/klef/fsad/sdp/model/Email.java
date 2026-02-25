package com.klef.fsad.sdp.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "email_details")
public class Email {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private int id;
    @Column(nullable = false) private String recipient;
    @Column(nullable = false) private String subject;
    @Column(nullable = false, length = 1000) private String message;
    @Column(nullable = false) private LocalDateTime sentAt;
    @Column(nullable = false) private String status;

    @PrePersist public void beforeSave() { this.sentAt = LocalDateTime.now(); this.status = "SUCCESS"; }
    
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setMessage(String message) { this.message = message; }
}