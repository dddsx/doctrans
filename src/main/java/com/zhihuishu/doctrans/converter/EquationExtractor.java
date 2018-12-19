package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.model.OMathData;
import com.zhihuishu.doctrans.model.WmfData;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.Map;

public interface EquationExtractor {
    
    /**
     * 提取wmf矢量图格式的公式
     */
    Map<String, OMathData> extractWMF(XWPFDocument document);
    
    /**
     * 提取用omath表示的公式
     */
    Map<String, WmfData> extractMathML(XWPFDocument document);
    
}
