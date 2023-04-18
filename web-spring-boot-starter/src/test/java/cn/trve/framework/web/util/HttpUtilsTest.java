package cn.trve.framework.web.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class HttpUtilsTest {

    @Test
    void doGet() throws IOException, InterruptedException {
        String s = HttpUtils.doGet("http://www.baidu.com");
        System.out.println(s);
    }

    @Test
    void doPost() {
    }

    @Test
    void doUploadStringResponseAsync() {
    }
}