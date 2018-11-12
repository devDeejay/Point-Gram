package com.devdelhi.pointgram.pointgram.model;

/**
 * Created by deejay on 12/3/18.
 */

public class messages {

    String message;
    String from;

    public messages(String message, String from, Boolean seen, long time, String type) {
        this.message = message;
        this.from = from;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    Boolean seen;
    long time;
    String type;

    public messages() {}


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
