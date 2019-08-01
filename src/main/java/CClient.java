/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

import java.io.IOException;

/**
 * @FileName: CCLIENT.java
 * @Description: CCLIENT.java类说明
 * @Author: guohao
 * @Date: 2019/8/1 12:49
 */
public class CClient {
    public static void main(String[] args) throws IOException {
        new NioClient().start ("CClient");
    }
}
