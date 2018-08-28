package com.lehua.tablet0306.utils;

import java.io.Serializable;

/**
 * Created by pendragon on 17-4-17.
 */

public class PushMessage implements Serializable,Comparable<PushMessage>{


    private String id;
    private String title;
    private String brief;
    private String date;
    private String photo = "";
    private int collected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public PushMessage() {
    }

    public PushMessage(String id, String title, String brief, String date, String photo) {
        this.id = id;
        this.title = title;
        this.brief = brief;
        this.date = date;
        this.photo = photo;
    }

    @Override
    public int compareTo(PushMessage p) {
        return p.date.compareTo(this.date);
    }
}
