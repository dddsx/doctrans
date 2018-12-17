package com.zhihuishu.doctrans.support;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.List;

public class XWPFUtils {
    
    private static int mathmlNum = 0;
    
    public static void extractMathMLInParagraph(XWPFParagraph p) {
        List<CTOMath> ctoMathList = p.getCTP().getOMathList();
        List<CTOMathPara> ctoMathParaList = p.getCTP().getOMathParaList();
        for (CTOMath ctoMath : ctoMathList) {
            try {
               // convertOmathToPNG(ctoMath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (CTOMathPara ctoMathPara : ctoMathParaList) {
            for (CTOMath ctoMath : ctoMathPara.getOMathList()) {
                try {
                    // convertOmathToPNG(ctoMath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String createRefPlaceholder(String ref){
        return "{sharp:" + ref + "}";
    }
    
    private static void printParagraphAttr(XWPFParagraph paragraph) {
        Class clazz = paragraph.getClass();
        for (Method method : clazz.getMethods()){
            if(!method.getName().startsWith("get")){
                continue;
            }
            String returnTypeName = method.getGenericReturnType().getTypeName();
            if(returnTypeName.equals(String.class.getTypeName()) || returnTypeName.equals(BigInteger.class.getTypeName())
                    || returnTypeName.equals("int")) {
                if(method.getParameterTypes().length == 0) {
                    System.out.print(method.getName().substring(3) + ":");
                    try {
                        System.out.println(method.invoke(paragraph));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
