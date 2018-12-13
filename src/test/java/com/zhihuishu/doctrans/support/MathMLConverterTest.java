package com.zhihuishu.doctrans.support;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.Test;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class MathMLConverterTest {
    
    private final static String MATH_PNG = "math";
    
    @Test
    public void testConvertOmathToPNG() throws Exception {
        File word = new File(getClass().getResource("omath.docx").getFile());
        XWPFDocument document = new XWPFDocument(new FileInputStream(word));
        List<XWPFParagraph> ps = document.getParagraphs();
        int num = 1;
        for (XWPFParagraph p : ps) {
            List<CTOMath> maths = p.getCTP().getOMathList();
            List<CTOMathPara> mathParas = p.getCTP().getOMathParaList();
            for (CTOMath ctoMath : maths) {
                File png = new File(word.getParentFile(), MATH_PNG + num++ + Constant.EXT_PNG);
                MathMLConverter.convertOmathToPNG(ctoMath, png);
            }
            for (CTOMathPara ctoMathPara : mathParas) {
                for (CTOMath ctoMath : ctoMathPara.getOMathList()) {
                    File png = new File(word.getParentFile(), MATH_PNG + num++ + Constant.EXT_PNG);
                    MathMLConverter.convertOmathToPNG(ctoMath, png);
                }
            }
        }
    }
}
