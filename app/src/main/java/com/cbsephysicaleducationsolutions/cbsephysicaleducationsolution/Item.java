package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import com.google.firebase.Timestamp;

public class Item {

    private String title;
    private String desc;
    private String link;
    private String image;
    private String pdf;
    private int grade;
    private com.google.firebase.Timestamp created;

    public Item(String title, String desc, String link, String image, String pdf, int grade, com.google.firebase.Timestamp created) {
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.image = image;
        this.pdf = pdf;
        this.grade = grade;
        this.created = created;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", pdf='" + pdf + '\'' +
                ", grade=" + grade +
                ", created=" + created +
                '}';
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setCreated(com.google.firebase.Timestamp created) {
        this.created = created;
    }


    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }

    public String getPdf() {
        return pdf;
    }

    public int getGrade() {
        return grade;
    }

    public Timestamp getCreated() {
        return created;
    }
}
