package com.zhihuishu.doctrans.util;

import java.util.UUID;

public class UUIDUtils {

    public static String filenameUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
