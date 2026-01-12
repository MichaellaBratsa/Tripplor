package com.mbrats01.epl498_group_project.ui.reviews;

public class Review {

    private String id;
    private String attractionId;
    private String userName;
    private String comment;
    private int rating;        // 1..5
    private long createdAt;    // millis

    public Review() {
        // needed for Firebase
    }

    public Review(String id,
                  String attractionId,
                  String userName,
                  String comment,
                  int rating,
                  long createdAt) {
        this.id = id;
        this.attractionId = attractionId;
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getAttractionId() { return attractionId; }
    public String getUserName() { return userName; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }
    public long getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setAttractionId(String attractionId) { this.attractionId = attractionId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setComment(String comment) { this.comment = comment; }
    public void setRating(int rating) { this.rating = rating; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
