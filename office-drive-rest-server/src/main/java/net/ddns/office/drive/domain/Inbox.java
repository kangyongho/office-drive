package net.ddns.office.drive.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by NPOST on 2017-06-12.
 */
@Entity
public class Inbox {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "INBOX_ID")
    private Long id;

    private String sender;
    private String receiver;
    private String title;
    private String message;

    @Column
    private LocalDateTime date;

    public Inbox() {
    }

    public Inbox(String sender, String receiver, String title, String message, LocalDateTime date) {
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
