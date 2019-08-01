/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

import java.io.IOException;

/**
 * @FileName: BClient.java
 * @Description: BClient.java类说明
 * @Author: guohao
 * @Date: 2019/8/1 12:48
 */
public class BClient {

    public static void main(String[] args) throws IOException {
        new NioClient().start ("BClient");
    }
}
