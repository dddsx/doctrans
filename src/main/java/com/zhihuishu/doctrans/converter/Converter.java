package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.OutputStream;

public interface Converter {
    
    void convert(XWPFDocument document, OutputStream out, ConvertSetting settings);
    
}
