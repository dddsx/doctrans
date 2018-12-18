package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.model.MathMLData;
import com.zhihuishu.doctrans.model.WMFData;
import com.zhihuishu.doctrans.support.MathMLExtractor;
import com.zhihuishu.doctrans.support.WMFExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.List;

/**
 * 提取document中用wmf和omath表示的公式。方法是在提取处设置CTR占位符，并将提取到元数据返回。
 */
public class PlaceholderEquationExtractor implements EquationExtractor {
    
    private MathMLExtractor mathMLParser = new MathMLExtractor();
    
    private WMFExtractor wmfParser = new WMFExtractor();
    
    @Override
    public List<MathMLData> extractWMF(XWPFDocument document) {
        return mathMLParser.extractMathML(document);
    }
    
    @Override
    public List<WMFData> extractMathML(XWPFDocument document) {
        return wmfParser.extractWMF(document);
    }
}
