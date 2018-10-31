package com.zhihuishu.doctrans;

import com.zhihuishu.doctrans.model.Shape;
import com.zhihuishu.doctrans.utils.Constant;
import com.zhihuishu.doctrans.utils.ImgConverter;
import com.zhihuishu.doctrans.utils.MyFileUtil;
import com.zhihuishu.doctrans.utils.XWPFUtils;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DocxToHtmlConverter {

    private static final String HTML_PATH = "/data/html/";

    private static final String IMAGE_PATH = "/data/image/word/media/";

    /**
     * 将docx文件转换为带图文的html
     * @param inputStream docx文件输入流
     * @return html字符串
     */
    public static String docx2html(InputStream inputStream) {
        String htmlResult = "";
        File htmlOutputFile = new File(HTML_PATH + UUID.randomUUID().toString() + Constant.EXT_HTML);
        try (InputStream docxInputStream = inputStream;
             Writer writer = new OutputStreamWriter(FileUtils.openOutputStream(htmlOutputFile), StandardCharsets.UTF_8))
        {
            XWPFDocument document = new XWPFDocument(docxInputStream);
            List<XWPFParagraph> paragraphList = document.getParagraphs();
            Map<String, Shape> shapeMap = new HashMap<>();
            for (XWPFParagraph paragraph : paragraphList) {
                List<Shape> shapeList = XWPFUtils.extractShapeInParagraph(paragraph);
                if(shapeList == null || shapeList.size() == 0){
                    continue;
                }
                for (Shape shape : shapeList) {
                    XWPFPictureData pictureData = document.getPictureDataByID(shape.getRef());
                    String imageName = pictureData.getFileName();
                    shape.setImgName(imageName);
                    shapeMap.put(imageName, shape);
                }
            }

            List<File> imgFileList = new ArrayList<>();
            List<XWPFPictureData> pictures = document.getAllPictures();
            for (XWPFPictureData picture : pictures) {
                try {
                    File img = new File(IMAGE_PATH + picture.getFileName());
                    imgFileList.add(img);
                    FileUtils.writeByteArrayToFile(img, picture.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            XHTMLOptions options = XHTMLOptions.create();
            options.setImageManager(new ImageManager(new File(IMAGE_PATH), ""));
            options.setFragment(true);
            options.setIgnoreStylesIfUnused(true);
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, writer, options);
            htmlResult = FileUtils.readFileToString(htmlOutputFile, "UTF-8");
            htmlOutputFile.delete();

            for (File imgFile : imgFileList) {
                String imgName = imgFile.getName();
                if(FilenameUtils.isExtension(imgName, Constant.FORMAT_WMF)){
                    File svgFile = new File(IMAGE_PATH, FilenameUtils.getBaseName(imgName) + Constant.EXT_SVG);
                    File pngFile = new File(IMAGE_PATH, FilenameUtils.getBaseName(imgName) + Constant.EXT_PNG);
                    ImgConverter.convertWmf2Svg(imgFile, svgFile);
                    ImgConverter.convertSvg2Png(svgFile, pngFile);
                    String imgOssUrl = MyFileUtil.uploadFileToOSS(pngFile);
                    svgFile.delete();
                    pngFile.delete();
                    Shape shape = shapeMap.get(imgName);
                    String imgRefPlaceholder = XWPFUtils.createRefPlaceholder(shape.getRef());
                    htmlResult = htmlResult.replace(imgRefPlaceholder, createImgTag(imgOssUrl, shape.getStyle()));
                } else {
                    String imgOssUrl = MyFileUtil.uploadFileToOSS(imgFile);
                    String imgHtmlUrl = File.separator + imgFile.getName();
                    htmlResult = htmlResult.replace(imgHtmlUrl, imgOssUrl);
                }
                imgFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlResult;
    }

    private static String createImgTag(String url, String style){
        if(url == null){
            url = "";
        }
        if(style == null){
            style = "";
        }
        return "<img src=\"" + url + "\" style=\"" + style + "\">";
    }
}
