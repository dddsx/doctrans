package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.model.MathMLData;
import com.zhihuishu.doctrans.model.WMFData;
import com.zhihuishu.doctrans.support.MathMLParser;
import com.zhihuishu.doctrans.support.WMFParser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSimpleDocxConverter implements SimpleDocxConverter {
    
    protected Map<String, byte[]> imageDatas = new HashMap<>();
    
    protected List<WMFData> wmfDatas = new ArrayList<>();
    
    protected List<MathMLData> mathMLDatas = new ArrayList<>();
    
    @Override
    public void extractWMF(XWPFDocument document) {
        WMFParser.extractWMF(document, wmfDatas);
    }
    
    @Override
    public void extractMathML(XWPFDocument document) {
        MathMLParser.extractMathML(document, mathMLDatas);
    }
}
