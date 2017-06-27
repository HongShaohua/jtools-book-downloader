package com.hongshaohua.jtools.bookdownloader.main;

/**
 * Created by Aska on 2017/6/12.
 *
 * book_download.jar <workspace> <method>
 * workspace xxx_dir
 * method
 * --download -url http://xxx.html -path xxx.xml
 * --catalog -url http://xxx.html
 * --catalog -path xxx.html
 * --chapter -url http://xxx.html
 * --chapter -path xxx.html
 *
 */
public class Command {

    private String workspace;

    public static Command parse(String[] args) {

        return null;
    }
}
