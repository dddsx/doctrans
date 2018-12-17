package com.zhihuishu.doctrans.support;

import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTShape;
import com.zhihuishu.doctrans.model.WMFData;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import java.util.List;

public class WMFParser {
    
    public static void extractWMF(XWPFDocument document, List<WMFData> wmfDatas) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            extractWMFInRuns(document, wmfDatas, runs);
        }
        
    }
    
    private static void extractWMFInRuns(XWPFDocument document, List<WMFData> wmfDatas, List<XWPFRun> runs) {
        for (XWPFRun run : runs) {
            CTR ctr = run.getCTR();
            XmlCursor c = ctr.newCursor();
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (o instanceof CTDrawing) {
                    /* CTDrawing drawing = (CTDrawing) o;
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
                              S  imageBundleList.add(picture.getBlipFill().getBlip().getEmbed());
                            }
                        }
                    }*/
                } else if (o instanceof CTObject) {
                    CTObject object = (CTObject) o;
                    XmlCursor w = object.newCursor();
                    w.selectPath("./*");
                    while (w.toNextSelection()) {
                        XmlObject xmlObject = w.getObject();
                        if (xmlObject instanceof CTShape) {
                            CTShape shape = (CTShape) xmlObject;
                            CTImageData imageData = shape.getImagedataArray(0);
                            String rId = imageData.getId2();
                            XWPFPictureData pictureData = document.getPictureDataByID(rId);
                            wmfDatas.add(new WMFData(rId, pictureData.getData(), shape.getStyle()));
                            createWMFPlaceholder(run, rId);
                        }
                    }
                }
            }
        }
    }
    
    private static void createWMFPlaceholder(XWPFRun run, String rId) {
        run.setText(PlaceholderHelper.createWMFPlaceholder(rId));
    }
}
