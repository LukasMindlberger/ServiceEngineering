package jku.serviceengineering.androidblogservice;

/**
 * Created by Lukas Mindlberger on 08.01.2017.
 */

public class Blog {
    private String title;
    private String desc;

    private String authorMail;
    private String id;
    private String date;
    private String group;

    public Blog() {

    }

    public Blog(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public Blog(String title, String desc, String id, String authorMail, String date, String group) {
        this.title = title;
        this.desc = desc;

        this.id = id;
        this.authorMail = authorMail;
        this.date = date;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthorMail() {
        return authorMail;
    }

    public void setAuthorMail(String authorMail) {
        this.authorMail = authorMail;
    }

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
