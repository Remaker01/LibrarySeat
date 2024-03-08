package com.libraryseat.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class Message {
    private int uid;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp time;
    private String title,content;

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (uid != message.uid) return false;
        return time != null ? time.equals(message.time) : message.time == null;
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
