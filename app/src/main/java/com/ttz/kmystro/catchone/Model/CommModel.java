package com.ttz.kmystro.catchone.Model;


public class CommModel {

    public CommModel(){

    }

    private String Author;
    private String Comment;
    private String Id;

    public CommModel(String author, String comment, String id) {
        Author = author;
        Comment = comment;
        Id = id;
    }

    public String getAuthor() {
        return Author;
    }

    public String getComment() {
        return Comment;
    }

    public String getId() {
        return Id;
    }




}
