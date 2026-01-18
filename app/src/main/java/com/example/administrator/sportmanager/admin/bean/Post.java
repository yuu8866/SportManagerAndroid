package com.example.administrator.sportmanager.admin.bean;

/**
 * 社区帖子实体
 */
public class Post {

    private long id;
    private String title;
    private String content;
    private String username;
    private String createTime;

    public Post() {
    }

    public Post(long id, String title, String content, String username, String createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
