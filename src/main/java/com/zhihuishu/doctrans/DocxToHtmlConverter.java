package com.zhihuishu.doctrans;

import com.zhihuishu.doctrans.utils.MyFileUtil;
import com.zhihuishu.doctrans.utils.XWPFUtils;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
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

    private static final String DATA_HTML = "/data/html/";

    private static final String DATA_IMAGE = "/data/image/word/media/";

    private static final String DATA_DOMNLOAD = "/data/download/";

    /**
     * 将docx文件识别为html
     * @param inputStream
     * @param url 比如：http://file.zhihuishu.com/zhs_yufa_150820/ablecommons/demo/201809/3404484743d64189ba968d6328161c9f.docx
     * @return html字符串
     */
    public static String docx2html(InputStream inputStream, String url) {
        String htmlResult = "";
        File docx = null;
        // 从url或输入流中读取文件
        if (!StringUtils.isEmpty(url)) {
            docx = MyFileUtil.downloadFile(url, DATA_DOMNLOAD);
        } else {
            try {
                docx = new File(DATA_DOMNLOAD, UUID.randomUUID().toString());
                FileUtils.copyInputStreamToFile(inputStream, docx);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        String filename = docx.getName();
        if (!StringUtils.isEmpty(filename)) {
            filename = filename.split("\\.")[0];
        }

        String htmlFilename = DATA_HTML + filename + ".html";
        File htmlFile = new File(htmlFilename);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(FileUtils.openOutputStream(htmlFile), "utf-8")){
            XWPFDocument document = new XWPFDocument(new FileInputStream(docx));
            // 读取位图位置，设置占位标记"{sharp:rIdX}"，记录rId以及对应的文件名
            List<XWPFParagraph> paragraphList = document.getParagraphs();
            Map<String, String> imgRefMap = new HashMap<>();
            for (XWPFParagraph aParagraphList : paragraphList) {
                List<String> imageBundleList = XWPFUtils.readImageInParagraph(aParagraphList);
                if(imageBundleList != null && imageBundleList.size() > 0){
                    continue;
                }
                for (String pictureId : imageBundleList) {
                    XWPFPictureData pictureData = document.getPictureDataByID(pictureId);
                    String imageName = pictureData.getFileName();
                    // String lastParagraphText = paragraphList.get(i-1).getParagraphText();
                    imgRefMap.put(pictureId, imageName);
                }
            }

            // 文档图片内容
            List<XWPFPictureData> pictures = document.getAllPictures();
            for (XWPFPictureData picture : pictures) {
                try {
                    File imgFile = new File(DATA_IMAGE + picture.getFileName());
                    FileUtils.writeByteArrayToFile(imgFile, picture.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            XHTMLOptions options = XHTMLOptions.create();
            options.setImageManager(new ImageManager(new File(DATA_IMAGE), ""));
            options.setFragment(true);
            options.setIgnoreStylesIfUnused(true);
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, outputStreamWriter, options);

            htmlResult = FileUtils.readFileToString(htmlFile, "UTF-8");

            //todo 矢量图替换
            File imgDir = new File(DATA_IMAGE);
            File[] imgs = imgDir.listFiles();
            for (File imgFile : imgs) {
                //若非目录(即文件)，则打印
                if (!imgFile.isDirectory()) {
                    String ossUrl = MyFileUtil.uploadFileToOSS(imgFile);
                    String path = imgFile.toString();
                    String imgUrl = imgFile.toString().substring(path.lastIndexOf(File.separator));
                    htmlResult = htmlResult.replace(imgUrl, ossUrl);
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
