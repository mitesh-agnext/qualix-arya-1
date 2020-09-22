package com.custom.app.data.model.graph;

public class GraphItem {

    private int color;
    private String title;
    private String count;

    public GraphItem(String title, String count) {
        this.title = title;
        this.count = count;
    }

    public GraphItem(int color, String title, String count) {
        this.color = color;
        this.title = title;
        this.count = count;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public String getCount() {
        return count;
    }
}