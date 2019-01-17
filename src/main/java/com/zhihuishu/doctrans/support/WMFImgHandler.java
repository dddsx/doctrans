package com.zhihuishu.doctrans.support;

import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTShape;
import com.zhihuishu.doctrans.model.WmfData;
import com.zhihuishu.doctrans.util.RegexHelper;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static com.zhihuishu.doctrans.util.LengthUnitUtils.*;
import static org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_EMF;
import static org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_WMF;

/**
 * 提取document中WMF图片公式。方法是在提取处设置CTR占位符，并收集WMF图片元数据，普通图片则交由xdocreport进行默认处理。
 * 图片的表示方式有多种，以下列出所有已知情况(只写出重要元素):
 * 1.w:drawing方式
 *
 *     1.1 wp:anchor方式
 *       <w:drawing>
 *         <wp:anchor>
 *           <a:graphic>
 *             <a:graphicData>
 *               <pic:pic>
 *                 <pic:blipFill>
 *                   <a:blip r:embed="rId14"/>
 *                 </pic:blipFill>
 *                 <pic:spPr>
 *                   <a:xfrm>
 *                     <a:xfrm>
 *                       <a:ext cx="962660" cy="1362075"/>
 *                     </a:xfrm>
 *                   </a:xfrm>
 *                 </pic:spPr>
 *               </pic:pic>
 *             </a:graphicData>
 *           </a:graphic>
 *         </wp:anchor>
 *       </w:drawing>
 *
 *     1.2wp:inline方式
 *       <w:drawing>
 *         <wp:inline>
 *           <a:graphic>
 *             <a:graphicData>
 *               <pic:pic>
 *                 <pic:blipFill>
 *                   <a:blip r:embed="rId14"/>
 *                 </pic:blipFill>
 *                 <pic:spPr>
 *                   <a:xfrm>
 *                     <a:xfrm>
 *                       <a:ext cx="962660" cy="1362075"/>
 *                     </a:xfrm>
 *                   </a:xfrm>
 *                 </pic:spPr>
 *               </pic:pic>
 *             </a:graphic>
 *           </a:graphicData>
 *         </wp:inline>
 *       </w:drawing>
 *
 * 2.w:object(一般这里面就是矢量图了)
 *
 *   <w:object>
 *     <v:shape style="height:183pt;width:138.75pt;">
 *       <v:imagedata r:id="rId11"/> <!-- 这个rId才是wmf图片的 -->
 *     </v:shape>
 *     <o:OLEObject Type="Embed" r:id="rId10">
 *       <o:LockedField>false</o:LockedField>
 *     </o:OLEObject>
 *   </w:object>
 *
 * 3.w:pict
 *
 *   <w:object>
 *     <v:shape style="height:183pt;width:138.75pt;">
 *       <v:imagedata r:id="rId30"/> <!-- 这个rId才是wmf图片的 -->
 *     </v:shape>
 *     <o:OLEObject Type="Embed" r:id="rId29">
 *       <o:LockedField>false</o:LockedField>
 *     </o:OLEObject>
 *   </w:object>
 */
public class WMFImgHandler {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private XWPFDocument document;
    
    /**
     * placeholder map to WmfData
     */
    private Map<String, WmfData> wmfDatas = new HashMap<>();
    
    public WMFImgHandler(XWPFDocument document) {
        this.document = document;
    }
    
    /**
     * 提取document中的wmf元数据, 并设置占位符
     */
    public Map<String, WmfData> extractWMF() {
        List<IBodyElement> bodyElements = document.getBodyElements();
        for (IBodyElement bodyElement : bodyElements) {
            switch (bodyElement.getElementType()) {
                case PARAGRAPH:
                    XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
                    visitParagraph(paragraph);
                    break;
                case TABLE:
                    XWPFTable table = (XWPFTable) bodyElement;
                    visitTable(table);
                    break;
                case CONTENTCONTROL:
                    // ignore
                    break;
                default:
                    break;
            }
        }
        return wmfDatas;
    }
    
    /**
     * 提取table中的wmf, 遍历每一个单元格cell
     */
    private void visitTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                visitTableCell(cell);
            }
        }
    }
    
    /**
     * 提取cell中的wmf, 注意cell中可能会嵌套表格
     */
    private void visitTableCell(XWPFTableCell cell) {
        List<XWPFTable> tables = cell.getTables();
        for (XWPFTable table : tables) {
            visitTable(table);
        }
        List<XWPFParagraph> paragraphs = cell.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            visitParagraph(paragraph);
        }
    }
    
    /**
     * 提取paragraph中的wmf
     */
    private void visitParagraph(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (XWPFRun run : runs) {
            try {
                visitRun(run);
            } catch (Exception e) {
                logger.error("提取wmf图片时出现异常", e);
            }
        }
    }
    
    /**
     * 提取run中的wmf(目前发现的图片都是存在run中)
     */
    private void visitRun(XWPFRun run) {
        CTR ctr = run.getCTR();
        
        XmlCursor c = ctr.newCursor();
        try {
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (o instanceof CTDrawing) {
                    visitCTDrawing((CTDrawing) o, run);
                } else if (o instanceof CTObject) {
                    // <w:object>类型处理
                    CTObject object = (CTObject) o;
                    XmlCursor w = object.newCursor();
                    try {
                        w.selectPath("./*");
                        while (w.toNextSelection()) {
                            XmlObject xmlObject = w.getObject();
                            // <v:shape>, 里面一般都是wmf格式图片
                            if (xmlObject instanceof CTShape) {
                                visitCTSharp((CTShape) xmlObject, run);
                            }
                        }
                    } finally {
                        w.dispose();
                    }
    
                } else if (o instanceof org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture) {
                    // <w:pict>类型处理
                    org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture pict =
                            (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture) o;
                    XmlCursor w = pict.newCursor();
                    try {
                        w.selectPath("./*");
                        while (w.toNextSelection()) {
                            XmlObject xmlObject = w.getObject();
                            // <v:shape>, 里面一般都是wmf格式图片
                            if (xmlObject instanceof CTShape) {
                                visitCTSharp((CTShape) xmlObject, run);
                            }
                        }
                    } finally {
                        w.dispose();
                    }
                }
            }
        } finally {
            // 必须显式关闭游标资源，该问题曾经导致过死循环!
            c.dispose();
        }
    }
    
    /**
     * 解析<w:drawing>
     */
    private void visitCTDrawing(CTDrawing ctDrawing, XWPFRun run) {
        List<CTInline> ctInlines = ctDrawing.getInlineList();
        for (int i = 0; i < ctInlines.size(); i++) {
            boolean hasHandleImg = visitInline(ctInlines.get(i), run);
            if (hasHandleImg) {
                ctDrawing.removeInline(i);
            }
        }
        
        List<CTAnchor> ctAnchors = ctDrawing.getAnchorList();
        for (int i = 0; i < ctAnchors.size(); i++) {
            boolean hasHandleImg = visitAnchor(ctAnchors.get(i), run);
            if (hasHandleImg) {
                ctDrawing.removeAnchor(i);
            }
        }
    }
    
    /**
     * 从<wp:inline>中提取wmf图片数据，并设置占位符
     * @return 如果里面是wmf图片并且已经进行处理, 返回true
     */
    private boolean visitInline(CTInline ctInline, XWPFRun run) {
        // <wp:extent>
        // CTPositiveSize2D ctPositiveSize2D = ctInline.getExtent();
        // ctPositiveSize2D.getCx();
    
        // <a:graphic>
        CTGraphicalObject graphicalObject = ctInline.getGraphic();
        if (graphicalObject == null) {
            return false;
        }
    
        // <a:graphicData>
        CTGraphicalObjectData graphicData = graphicalObject.getGraphicData();
        if (graphicData == null) {
            return false;
        }
        
        XmlCursor cursor = graphicData.newCursor();
        try {
            cursor.selectPath("./*");
            while (cursor.toNextSelection()) {
                XmlObject xmlObject = cursor.getObject();
                // <pic:pic>
                if (xmlObject instanceof CTPicture) {
                    CTPicture picture = (CTPicture) xmlObject;
                    // <a:ext>, 内含图片的高度宽度参数
                    CTPositiveSize2D ext = picture.getSpPr().getXfrm().getExt();
                    double width = emu2points(ext.getCx());
                    double height = emu2points(ext.getCy());
                
                    String blipID = picture.getBlipFill().getBlip().getEmbed();
                    XWPFPictureData pictureData = document.getPictureDataByID(blipID);
                    if (pictureData == null) {
                        return false;
                    }
                    
                    String pictureName = pictureData.getFileName();
                    int pictureStyle = pictureData.getPictureType();
                
                    // 只处理wmf格式的图片, 其它格式的图片交由xdocreport进行默认处理
                    if (pictureStyle == PICTURE_TYPE_EMF || pictureStyle == PICTURE_TYPE_WMF) {
                        String placeholder = PlaceholderHelper.createWMFPlaceholder(pictureName);
                        wmfDatas.put(placeholder, new WmfData(placeholder, pictureData.getData(), width, height));
                        setWMFPlaceholder(run, placeholder);
                        return true;
                    }
                }
            }
        } finally {
            cursor.dispose();
        }
        return false;
    }
    
    /**
     * 从<wp:anchor>中提取wmf图片数据，并设置占位符
     * @return 如果里面是wmf图片并且已经进行处理, 返回true
     */
    private boolean visitAnchor(CTAnchor anchor, XWPFRun run) {
        CTGraphicalObject graphic = anchor.getGraphic();
        if (graphic == null) {
            return false;
        }
        
        CTGraphicalObjectData graphicData = graphic.getGraphicData();
        if (graphicData == null) {
            return false;
        }
        
        XmlCursor cursor = graphicData.newCursor();
        try {
            cursor.selectPath( "./*" );
            while (cursor.toNextSelection()) {
                XmlObject xmlObject = cursor.getObject();
                // <pic:pic>
                if (xmlObject instanceof CTPicture) {
                    CTPicture picture = (CTPicture) xmlObject;
                    // <a:ext>, 内含图片的高度宽度参数
                    CTPositiveSize2D ext = picture.getSpPr().getXfrm().getExt();
                    double width = emu2points(ext.getCx());
                    double height = emu2points(ext.getCy());
                
                    String blipID = picture.getBlipFill().getBlip().getEmbed();
                    XWPFPictureData pictureData = document.getPictureDataByID(blipID);
                    if (pictureData == null) {
                        return false;
                    }
                    
                    String pictureName = pictureData.getFileName();
                    int pictureStyle = pictureData.getPictureType();
                
                    // 只处理wmf格式的图片, 其它格式的图片交由xdocreport进行默认处理
                    if (pictureStyle == PICTURE_TYPE_EMF || pictureStyle == PICTURE_TYPE_WMF) {
                        String placeholder = PlaceholderHelper.createWMFPlaceholder(pictureName);
                        wmfDatas.put(placeholder, new WmfData(placeholder, pictureData.getData(), width, height));
                        setWMFPlaceholder(run, placeholder);
                        return true;
                    }
                }
            }
        } finally {
            cursor.dispose();
        }
        return false;
    }
    
    /**
     * 从<v:shape>中提取wmf图片数据，并设置占位符
     */
    private void visitCTSharp(CTShape ctShape, XWPFRun run) {
        CTImageData imageData = ctShape.getImagedataArray(0);
        String blipID = imageData.getId2();
        XWPFPictureData pictureData = document.getPictureDataByID(blipID);
        int pictureStyle = pictureData.getPictureType();
        String pictureName = pictureData.getFileName();
        
        if (pictureStyle == PICTURE_TYPE_EMF || pictureStyle == PICTURE_TYPE_WMF) {
            String placeholder = PlaceholderHelper.createWMFPlaceholder(pictureName);
    
            // 解析wmf的高宽样式
            Double[] styles = parseWMFStyle(ctShape.getStyle());
            Double width = styles[0];
            Double height = styles[1];
    
            wmfDatas.put(placeholder, new WmfData(placeholder, pictureData.getData(), width, height));
            setWMFPlaceholder(run, placeholder);
        }
    }
    
    /**
     * 设置wmf图片占位符, 以便图片上传完成后进行替换
     */
    private void setWMFPlaceholder(XWPFRun run, String placeholder) {
        String text = run.getText(0);
        if (text == null) {
            run.setText(placeholder, 0);
        } else {
            run.setText(text + placeholder);
        }
    }
    
    private Double[] parseWMFStyle(String style) {
        Double[] styles = new Double[2];
        Matcher matcher;
        
        // 解析width样式，并将单位转为px
        try {
            if ((matcher = RegexHelper.widthValuePattern.matcher(style)).find()) {
                double num = Double.parseDouble(matcher.group(1));
                String measure = matcher.group(2);
                styles[0] = convertToPoints(measure, num);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        // 解析height样式，并将单位转为px
        try {
            if ((matcher = RegexHelper.heightValuePattern.matcher(style)).find()) {
                double num = Double.parseDouble(matcher.group(1));
                String measure = matcher.group(2);
                styles[1] = convertToPoints(measure, num);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        return styles;
    }
    
    private Double convertToPoints(String measure, double num) {
        Double points;
        switch (measure) {
            case INCH_UNIT:
                // points = inch2points(num); 样式中的英寸单位不够准确，先不读这个参数
                points = null;
                break;
            case PT_UNIT:
                points = pt2points(num);
                break;
            case PX_UNIT:
                points = num;
                break;
            default:
                points = null;
                break;
        }
        return points;
    }
}