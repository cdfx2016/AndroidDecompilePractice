package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

public class CookieResulte {
    private long id;
    private String resulte;
    private long time;
    private String url;

    public CookieResulte(String url, String resulte, long time) {
        this.url = url;
        this.resulte = resulte;
        this.time = time;
    }

    public CookieResulte(long id, String url, String resulte, long time) {
        this.id = id;
        this.url = url;
        this.resulte = resulte;
        this.time = time;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResulte() {
        return this.resulte;
    }

    public void setResulte(String resulte) {
        this.resulte = resulte;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
