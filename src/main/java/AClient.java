/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

import java.io.IOException;

/**
 * @FileName: AClient.java
 * @Description: AClient.java类说明
 * @Author: guohao
 * @Date: 2019/8/1 12:48
 */
public class AClient {
    public static void main(String[] args) throws IOException {
        new NioClient().start ("AClient");
    }
}
