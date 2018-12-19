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

import java.util.ArrayList;
import java.util.List;

public class WMFImgHandler {
    
    private List<WMFData> wmfDatas = new ArrayList<>();
    
    public List<WMFData> extractWMF(XWPFDocument document) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            extractWMFInRuns(document, runs);
        }
        return wmfDatas;
    }
    
    private void extractWMFInRuns(XWPFDocument document, List<XWPFRun> runs) {
        for (XWPFRun run : runs) {
            CTR ctr = run.getCTR();
            XmlCursor c = ctr.newCursor();
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (o instanceof CTDrawing) {
                    // 普通图片，不做处理
                } else if (o instanceof CTObject) {
                    CTObject object = (CTObject) o;
                    XmlCursor w = object.newCursor();
                    w.selectPath("./*");
                    while (w.toNextSelection()) {
                        XmlObject xmlObject = w.getObject();
                        if (xmlObject instanceof CTShape) {
                            // 如果是wmf格式图片
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
    
    private void createWMFPlaceholder(XWPFRun run, String rId) {
        CTR ctr = run.getCTR();
        run.setText(PlaceholderHelper.createWMFPlaceholder(rId));
    }
}