package com.hongshaohua.jtools.bookdownloader.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aska on 2017/6/12.
 */
public class BookAdapterBaquge extends BookAdapter {

    public BookAdapterBaquge(String dir, String book, String catalogUrl) {
        super("gb2312", dir, book, catalogUrl);
    }

    private String filename(String href) {
        return href.substring(href.lastIndexOf("/") + 1);
    }

    private Chapter catalog(Element aElm) throws Exception {
        String name = aElm.text();
        String filename = filename(aElm.attr("href"));
        String url = this.getCatalogUrl() + filename;
        return new Chapter(name, url, filename);
    }

    @Override
    public List<Chapter> catalog(String html) throws Exception {
        Document doc = Jsoup.parse(html);
        Elements aElms = doc.select("#list dd a");
        List<Chapter> chapters = new ArrayList<>();
        for(Element aElm : aElms) {
            Chapter chapter = catalog(aElm);
            if(chapter == null) {
                return null;
            }
            chapters.add(chapter);
        }
        return chapters;
    }

    @Override
    public String chapter(String html) throws Exception {
        Document doc = Jsoup.parse(html);
        Element contentElm = doc.getElementById("content");
        String content = contentElm.html();
        content = content.replace("&nbsp;", " ");
        content = content.replace("<br>", "\r\n");
        return content;
    }
}
