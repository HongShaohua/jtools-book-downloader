package com.hongshaohua.jtools.bookdownloader.main;

/**
 * Created by Aska on 2017/6/12.
 */
public class Chapter {

    private String name;
    private String url;
    private String filename;

    public Chapter(String name, String url, String filename) {
        this.name = name;
        this.url = url;
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }
}
