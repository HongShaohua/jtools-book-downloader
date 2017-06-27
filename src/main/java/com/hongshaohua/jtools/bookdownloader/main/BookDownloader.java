package com.hongshaohua.jtools.bookdownloader.main;

import com.hongshaohua.jtools.http.client.DefaultHttpClient;
import com.hongshaohua.jtools.http.client.DefaultHttpClientSimple;
import com.hongshaohua.jtools.http.client.DefaultHttpGetString;
import com.hongshaohua.jtools.task.TaskEngine;
import com.hongshaohua.jtools.task.TaskLevelNormal;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Aska on 2017/6/12.
 */
public class BookDownloader {

    private DefaultHttpClient httpClient;
    private TaskEngine taskEngine;
    private BookAdapter adapter;

    public BookDownloader(BookAdapter adapter) {
        this.httpClient = new DefaultHttpClientSimple();
        this.httpClient.setConnectionCount(1000);
        this.httpClient.open();
        this.taskEngine = new TaskEngine(100, 10000);
        this.adapter = adapter;
    }

    public BookDownloader init() {
        try {
            this.taskEngine.start();
            return this;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean download(String url, String path) {
        try {
            DefaultHttpGetString get = new DefaultHttpGetString(url, this.adapter.getCharset());
            this.httpClient.execute(get);
            if(get.isOk()) {
                String content = get.responseContent();
                File file = new File(path);
                if(file.exists()) {
                    file.delete();
                }
                file.getParentFile().mkdirs();
                FileUtils.write(file, content, "utf-8");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String downloadCatalogHtml() throws Exception {
        String catalogHtmlPath = this.adapter.getCatalogHtmlPath();
        File catalogHtmlFile = new File(catalogHtmlPath);
        if(!catalogHtmlFile.exists()) {
            if(!this.download(this.adapter.getCatalogUrl(), catalogHtmlPath)) {
                return null;
            }
        }
        return FileUtils.readFileToString(catalogHtmlFile, "utf-8");
    }

    private List<Chapter> catalog() throws Exception {
        String html = this.downloadCatalogHtml();
        if(html == null) {
            return null;
        }
        return this.adapter.catalog(html);
    }

    private class Counter {
        private int total;
        private int count;

        public Counter(int total) {
            this.total = total;
            this.count = 0;
        }

        public synchronized void print(String str) {
            this.count++;
            System.out.println(this.total + " / " + this.count + " " + str);
        }

        public synchronized boolean done() {
            if(this.count >= this.total) {
                return true;
            }
            return false;
        }
    }

    private class DownloadChapterHtmlTask extends TaskLevelNormal {
        private Counter counter;
        private Chapter chapter;

        public DownloadChapterHtmlTask(Counter counter, Chapter chapter) {
            this.counter = counter;
            this.chapter = chapter;
        }

        @Override
        public void execute() throws Exception {
            String chapterHtmlPath = adapter.getChapterHtmlPath(chapter);
            File chapterHtmlFile = new File(chapterHtmlPath);
            while(true) {
                if(chapterHtmlFile.exists()) {
                    this.counter.print(chapterHtmlPath);
                    return;
                }
                if(download(chapter.getUrl(), chapterHtmlPath)) {
                    this.counter.print(chapter.getUrl());
                    return;
                }
            }
        }
    }

    private boolean downloadChapterHtmls(List<Chapter> chapters) throws Exception {
        Counter counter = new Counter(chapters.size());
        for(int i = 0; i < chapters.size(); i++) {
            Chapter chapter = chapters.get(i);
            this.taskEngine.post(new DownloadChapterHtmlTask(counter, chapter));
        }
        while(true) {
            try {
                Thread.sleep(1000);
                if(counter.done()) {
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String chapterHtml(Chapter chapter) throws Exception {
        String chapterHtmlPath = this.adapter.getChapterHtmlPath(chapter);
        File chapterHtmlFile = new File(chapterHtmlPath);
        if(!chapterHtmlFile.exists()) {
            if(!this.download(chapter.getUrl(), chapterHtmlPath)) {
                return null;
            }
        }
        return FileUtils.readFileToString(chapterHtmlFile, "utf-8");
    }

    private String chapter(Chapter chapter) throws Exception {
        String html = this.chapterHtml(chapter);
        if(html == null) {
            return null;
        }
        return this.adapter.chapter(html);
    }

    private String chapters(List<Chapter> chapters) throws Exception {
        Counter counter = new Counter(chapters.size());
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < chapters.size(); i++) {
            Chapter chapter = chapters.get(i);
            String chapterContent = this.chapter(chapter);
            if(chapterContent == null || chapterContent.trim().isEmpty()) {
                return null;
            }

            strBuilder.append(chapter.getName());
            strBuilder.append("\r\n\r\n");
            strBuilder.append(chapterContent);
            strBuilder.append("\r\n\r\n\r\n");

            counter.print(chapter.getName());
        }
        return strBuilder.toString();
    }

    private void book(String content) throws Exception {
        File bookFile = new File(this.adapter.getBookPath());
        if(bookFile.exists()) {
            bookFile.delete();
        }
        FileUtils.write(bookFile, content, "utf-8");
    }

    public boolean go() {
        try {
            List<Chapter> chapters = this.catalog();
            if(chapters == null || chapters.isEmpty()) {
                return false;
            }
            if(!this.downloadChapterHtmls(chapters)) {
                return false;
            }
            String content = this.chapters(chapters);
            if(content == null || content.trim().isEmpty()) {
                return false;
            }
            this.book(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
