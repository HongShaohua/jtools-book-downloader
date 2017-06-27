package com.hongshaohua.jtools.bookdownloader.main;

import java.util.List;

/**
 * Created by Aska on 2017/6/12.
 */
public abstract class BookAdapter {

    private String charset;
    private String dir;
    private String book;
    private String catalogUrl;

    public BookAdapter(String charset, String dir, String book, String catalogUrl) {
        this.charset = charset;
        this.dir = dir;
        this.book = book;
        this.catalogUrl = catalogUrl;
    }

    public String getCharset() {
        return charset;
    }

    public String getDir() {
        return dir;
    }

    public String getBook() {
        return book;
    }

    public String getCatalogUrl() {
        return catalogUrl;
    }

    public String getBookPath() {
        return this.dir + this.book + ".txt";
    }

    public String getCatalogHtmlPath() {
        return this.dir + "catalog.html";
    }

    public String getChapterHtmlPath(Chapter chapter) {
        return this.dir + "chapters/" + chapter.getFilename();
    }

    public abstract List<Chapter> catalog(String html) throws Exception;

    public abstract String chapter(String html) throws Exception;
}
