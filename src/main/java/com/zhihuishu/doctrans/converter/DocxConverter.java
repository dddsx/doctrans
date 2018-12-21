package com.zhihuishu.doctrans.converter;

/**
 * docx转html接口
 */
public interface DocxConverter {
    
    /**
     * 将docx输入流转换为html字符串
     * @return html字符串
     */
    String convert();
}
