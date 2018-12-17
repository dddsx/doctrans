package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;

import java.io.InputStream;

/**
 * docx转html接口
 */
public interface DocxConverter {
    
    /**
     * 将docx输入流转换为html字符串
     * @param docxInput docx文档输入流
     * @param settings 转换设置
     * @return
     */
    String convert(InputStream docxInput, ConvertSetting settings);
}
