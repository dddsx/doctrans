package com.zhihuishu.doctrans.support;

import java.io.File;

public interface WMFConverter {
    
    /**
     * 将wmf图片转为svg图片
     * @param source wmf源文件
     * @param target 输出文件
     */
    void convertToSVG(File source, File target);

}
