package com.reactiveandroid.sample.mvp.models;

import android.support.annotation.NonNull;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.sample.AppDatabase;

import java.util.Date;
import java.util.List;

@Table(name = "Notes", database = AppDatabase.class)
public class Note extends Model {

    @PrimaryKey
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "text")
    private String text;
    @Column(name = "color")
    private int color;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;
    private List<Folder> folders;

    //ReActiveAndroid requires empty constructor
    public Note() {
    }

    public Note(String title, String text, int color) {
        this.title = title;
        this.text = text;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> noteFolders) {
        this.folders = noteFolders;
    }

    @NonNull
    @Override
    public Long save() {
        if (id == null) {
            createdAt = new Date();
        }

        updatedAt = new Date();
        return super.save();
    }

}