package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;

/**
 * docx转html接口
 */
public interface DocxConverter {
    
    /**
     * 将docx输入流转换为html字符串
     * @param settings 转换设置
     * @return html字符串
     */
    String convert(ConvertSetting settings);
}
