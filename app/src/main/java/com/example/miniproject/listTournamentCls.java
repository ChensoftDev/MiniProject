package com.example.miniproject;

public class listTournamentCls {

    private String name;
    private String category;
    private String difficulty;
    private String startdate;
    private String enddate;
    private Integer like;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }


    public listTournamentCls(String name, String category, String difficulty, String startdate, String enddate, Integer like,String status) {
        this.name = name;
        this.category = category;
        this.difficulty = difficulty;
        this.startdate = startdate;
        this.enddate = enddate;
        this.like = like;
        this.status = status;
    }
}
