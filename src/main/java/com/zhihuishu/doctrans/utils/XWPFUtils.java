package com.zhihuishu.doctrans.utils;

import com.microsoft.schemas.vml.CTShape;
import com.zhihuishu.doctrans.model.Shape;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import java.util.ArrayList;
import java.util.List;

public class XWPFUtils {

    /**
     * 读取段落中的图片信息，为矢量图设置占位符，并返回矢量图索引列表
     */
    public static List<Shape> extractShapeInParagraph(XWPFParagraph paragraph) {

        List<Shape> shapeList = new ArrayList<>();

        // 段落中所有XWPFRun
        List<XWPFRun> runList = paragraph.getRuns();
        for (XWPFRun run : runList) {
            // XWPFRun是POI对xml元素解析后生成的自己的属性，无法通过xml解析，需要先转化成CTR
            CTR ctr = run.getCTR();
            // 对子元素进行遍历
            XmlCursor c = ctr.newCursor();
            // 这个就是拿到所有的子元素：
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                // 如果子元素是<w:drawing>这样的形式，使用CTDrawing保存图片
/*                if (o instanceof CTDrawing) {
                    CTDrawing drawing = (CTDrawing) o;
                    CTInline[] ctInlines = drawing.getInlineArray();
                    for (CTInline ctInline : ctInlines) {
                        CTGraphicalObject graphic = ctInline.getGraphic();
                        //
                        XmlCursor cursor = graphic.getGraphicData().newCursor();
                        cursor.selectPath("./*");
                        while (cursor.toNextSelection()) {
                            XmlObject xmlObject = cursor.getObject();
                            // 如果子元素是<pic:pic>这样的形式
                            if (xmlObject instanceof CTPicture) {
                                org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture picture = (org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture) xmlObject;
                                //拿到元素的属性
                                imageBundleList.add(picture.getBlipFill().getBlip().getEmbed());
                            }
                        }
                    }
                }*/

                // 使用CTObject读取<w:object>形式的图片
                if (o instanceof CTObject) {
                    CTObject object = (CTObject) o;
                    // System.out.println(object);
                    XmlCursor w = object.newCursor();
                    w.selectPath("./*");
                    while (w.toNextSelection()) {
                        XmlObject xmlObject = w.getObject();
                        if (xmlObject instanceof CTShape) {
                            Shape s = new Shape();
                            CTShape shape = (CTShape) xmlObject;
                            String ref = shape.getImagedataArray()[0].getId2();
                            // 设置占位标记
                            run.setText(createRefPlaceholder(ref));
                            s.setRef(ref);
                            s.setStyle(shape.getStyle());
                            shapeList.add(s);
                        }
                    }
                }
            }
        }
        return shapeList;
    }

    public static String createRefPlaceholder(String ref){
        return "{sharp:" + ref + "}";
    }
}
