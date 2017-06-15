package net.ddns.office.drive.domain;

import java.util.Date;

/**
 * Created by NPOST on 2017-06-12.
 */
public class Message {

    private String to;
    private String from;
    private String title;
    private String message;
    private Date date;

    public Message() {
    }

    public Message(String to, String from, String title, String message, Date date) {
        this.to = to;
        this.from = from;
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
