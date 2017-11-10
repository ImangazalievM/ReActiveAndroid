package com.reactiveandroid.sample.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.ForeignKeyAction;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.sample.AppDatabase;

@Table(name = "NoteFolderRelations", database = AppDatabase.class)
public class NoteFolderRelation extends Model {

    @PrimaryKey
    private Long id;
    @Column(name = "note", onDelete = ForeignKeyAction.CASCADE)
    private Note note;
    @Column(name = "folder", onDelete = ForeignKeyAction.CASCADE)
    private Folder folder;


    public NoteFolderRelation() {
    }

    public NoteFolderRelation(Note note, Folder folder) {
        this.note = note;
        this.folder = folder;
    }

    public Note getNote() {
        return note;
    }

    public Folder getFolder() {
        return folder;
    }

}
