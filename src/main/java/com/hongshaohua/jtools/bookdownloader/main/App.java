package com.hongshaohua.jtools.bookdownloader.main;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Command command = Command.parse(args);
        new BookDownloader(new BookAdapterBaquge("./book/从零开始/", "从零开始", "http://www.baquge.tw/files/article/html/0/766/")).init().go();
        System.out.println( "finished" );
    }
}
