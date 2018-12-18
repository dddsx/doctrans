package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.model.MathMLData;
import com.zhihuishu.doctrans.model.WMFData;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.List;

public interface EquationExtractor {
    
    /**
     * 提取wmf矢量图格式的公式
     */
    List<MathMLData> extractWMF(XWPFDocument document);
    
    /**
     * 提取用omath表示的公式
     */
    List<WMFData> extractMathML(XWPFDocument document);
    
}
