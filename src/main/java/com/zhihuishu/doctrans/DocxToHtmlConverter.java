package com.zhihuishu.doctrans;

import com.zhihuishu.doctrans.utils.Constant;
import com.zhihuishu.doctrans.utils.MyFileUtil;
import com.zhihuishu.doctrans.utils.WmfConverter;
import com.zhihuishu.doctrans.utils.XWPFUtils;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DocxToHtmlConverter {

    private static final String HTML_PATH = "/data/html/";

    private static final String IMAGE_PATH = "/data/image/word/media/";

    private static final String DOMNLOAD_PATH = "/data/download/";

    /**
     * 将docx文件识别为html，两个参数需有其一不为null
     * @param inputStream
     * @param url 比如：http://file.zhihuishu.com/zhs_yufa_150820/ablecommons/demo/201809/3404484743d64189ba968d6328161c9f.docx
     * @return html字符串
     */
    public static String docx2html(InputStream inputStream, String url) {
        String htmlResult = "";
        File docx;
        // 从url或输入流中读取文件
        if (!StringUtils.isEmpty(url)) {
            docx = MyFileUtil.downloadFile(url, DOMNLOAD_PATH);
        } else {
            try {
                docx = new File(DOMNLOAD_PATH, UUID.randomUUID().toString() + Constant.EXT_DOCX);
                FileUtils.copyInputStreamToFile(inputStream, docx);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        String docxName = docx.getName();
        String htmlName = HTML_PATH + FilenameUtils.getBaseName(docxName) + Constant.EXT_HTML;
        File htmlFile = new File(htmlName);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(FileUtils.openOutputStream(htmlFile), "utf-8")){
            XWPFDocument document = new XWPFDocument(new FileInputStream(docx));
            // 读取位图位置，设置占位标记"{sharp:rIdX}"，记录rId以及对应的文件名
            List<XWPFParagraph> paragraphList = document.getParagraphs();
            Map<String, String> imgRefMap = new HashMap<>();
            for (XWPFParagraph paragraph : paragraphList) {
                List<String> imageBundleList = XWPFUtils.readImageInParagraph(paragraph);
                if(imageBundleList == null || imageBundleList.size() == 0){
                    continue;
                }
                for (String pictureId : imageBundleList) {
                    XWPFPictureData pictureData = document.getPictureDataByID(pictureId);
                    String imageName = pictureData.getFileName();
                    imgRefMap.put(imageName, pictureId);
                }
            }

            // 文档图片内容
            List<XWPFPictureData> pictures = document.getAllPictures();
            for (XWPFPictureData picture : pictures) {
                try {
                    File imgFile = new File(IMAGE_PATH + picture.getFileName());
                    FileUtils.writeByteArrayToFile(imgFile, picture.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            XHTMLOptions options = XHTMLOptions.create();
            options.setImageManager(new ImageManager(new File(IMAGE_PATH), ""));
            options.setFragment(true);
            options.setIgnoreStylesIfUnused(true);
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, outputStreamWriter, options);

            htmlResult = FileUtils.readFileToString(htmlFile, "UTF-8");

            File imgDir = new File(IMAGE_PATH);
            File[] imgs = imgDir.listFiles();
            for (File imgFile : imgs) {
                if (!imgFile.isDirectory()) {
                    String imgName = imgFile.getName();
                    if(FilenameUtils.isExtension(imgName, Constant.FORMAT_WMF)){
                        File svgFile = new File(IMAGE_PATH, FilenameUtils.getBaseName(imgName) + Constant.EXT_SVG);
                        WmfConverter.convertToSvg(imgFile, svgFile);
                        String imgOssUrl = MyFileUtil.uploadFileToOSS(svgFile);
                        String ref = imgRefMap.get(imgName);
                        String imgHtmlUrl = XWPFUtils.getRefPlaceholder(ref);
                        htmlResult = htmlResult.replace(imgHtmlUrl, imgOssUrl);
                        svgFile.delete();
                    } else {
                        String imgOssUrl = MyFileUtil.uploadFileToOSS(imgFile);
                        String path = imgFile.getPath();
                        String imgHtmlUrl = path.substring(path.lastIndexOf(File.separator));
                        htmlResult = htmlResult.replace(imgHtmlUrl, imgOssUrl);
                    }
                    imgFile.delete();
                }
            }
            //删除本地文件
            htmlFile.delete();
            docx.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlResult;
    }
}
