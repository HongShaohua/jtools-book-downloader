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
        new BookDownloader(new BookAdapterBaquge("./book/最强反派系统/", "最强反派系统", "http://www.baquge.tw/files/article/html/23/23078/")).init().go();
        System.out.println( "finished" );
    }
}
