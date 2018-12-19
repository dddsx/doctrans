package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.model.OMathData;
import com.zhihuishu.doctrans.model.WMFData;
import com.zhihuishu.doctrans.support.MathMLHandler;
import com.zhihuishu.doctrans.support.WMFImgHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.List;

/**
 * 提取document中用wmf和omath表示的公式。方法是在提取处设置CTR占位符，并将提取到元数据返回。
 */
public class PlaceholderEquationExtractor implements EquationExtractor {
    
    private MathMLHandler mathMLParser = new MathMLHandler();
    
    private WMFImgHandler wmfParser = new WMFImgHandler();
    
    @Override
    public List<OMathData> extractWMF(XWPFDocument document) {
        return mathMLParser.extractMathML(document);
    }
    
    @Override
    public List<WMFData> extractMathML(XWPFDocument document) {
        return wmfParser.extractWMF(document);
    }
}
