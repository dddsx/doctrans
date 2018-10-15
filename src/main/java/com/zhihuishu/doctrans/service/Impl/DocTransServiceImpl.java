package com.zhihuishu.doctrans.service.Impl;

import com.able.base.ftp.oss.OSSPublicUploadInterface;
import com.alibaba.fastjson.JSONObject;
import com.zhihuishu.doctrans.service.DocTransService;
import com.zhihuishu.doctrans.utils.CustomXWPFDocument;
import com.zhihuishu.doctrans.utils.MyFileUtil;
import com.zhihuishu.doctrans.utils.XWPFUtils;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

@Service
public class DocTransServiceImpl implements DocTransService {

    private static final String DATA_HTML = "/data/html/";

    private static final String DATA_IMAGE = "/data/image/";

    private static final String DATA_IMAGE_SOURCE = "/data/image/word/media/";

    private static final String DATA_DOMNLOAD = "/data/download/";

    //String sourceFileName = "C:\\Users\\able\\Documents\\Tencent Files\\870070823\\FileRecv\\wordtestpaper2.docx";
    //http://file.zhihuishu.com/zhs_yufa_150820/ablecommons/demo/201809/3404484743d64189ba968d6328161c9f.docx
    @Override
    public String docx2html(MultipartFile multipartFile, String url, HttpServletRequest request) {
        String htmlResult = "";
        File file = null;
        // 尝试从url下载
        if (!StringUtils.isEmpty(url)) {
            file = MyFileUtil.downloadFile(url, DATA_DOMNLOAD);
        }

        // 尝试从MultipartFile下载
        boolean hasInputFile = multipartFile != null && !StringUtils.isEmpty(multipartFile.getOriginalFilename())
                && multipartFile.getSize() > 0;
        if ((file == null || !file.exists()) && hasInputFile) {
            try {
                file = new File(DATA_DOMNLOAD, multipartFile.getOriginalFilename());
                FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        if (file == null) {
            return "";
        }

        String filename = file.getName();
        if (!StringUtils.isEmpty(filename)) {
            filename = filename.split("\\.")[0];
        }

        String htmlFilename = DATA_HTML + filename + ".html";
        File htmlFile = new File(htmlFilename);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(FileUtils.openOutputStream(htmlFile), "utf-8")){
            CustomXWPFDocument document = new CustomXWPFDocument(new FileInputStream(file));
            // 读取位图位置，并设置占位标记
            List<XWPFParagraph> paragraphList = document.getParagraphs();
            for (int i = 0; i < paragraphList.size(); i++) {
                List<String> imageBundleList = XWPFUtils.readImageInParagraph(paragraphList.get(i));
                if(!CollectionUtils.isEmpty(imageBundleList)){
                    for(String pictureId:imageBundleList){
                        XWPFPictureData pictureData = document.getPictureDataByID(pictureId);
                        String imageName = pictureData.getFileName();
                        String lastParagraphText = paragraphList.get(i-1).getParagraphText();
                        System.out.println(pictureId +"\t|" + imageName + "\t|" + lastParagraphText);
                    }
                }
            }

            // 文档图片内容
            List<XWPFPictureData> pictures = document.getAllPictures();
            for (XWPFPictureData picture : pictures) {
                try {
                    File imgFile = new File(DATA_IMAGE_SOURCE + picture.getFileName());
                    FileUtils.writeByteArrayToFile(imgFile, picture.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            XHTMLOptions options = XHTMLOptions.create();
            options.setImageManager(new ImageManager(null, DATA_IMAGE));
            options.setFragment(true);
            options.setIgnoreStylesIfUnused(false);
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, outputStreamWriter, options);

            htmlResult = FileUtils.readFileToString(htmlFile, "UTF-8");

            // 遍历文件夹中的图片
            // 获取其file对象
            File dataimg = new File(DATA_IMAGE_SOURCE);
            // 遍历path下的文件和目录，放在File数组中
            File[] fs = dataimg.listFiles();
            // 遍历File[]数组
            for (File f : fs) {
                //若非目录(即文件)，则打印
                if (!f.isDirectory()) {
                    try {
                        //上传到oss
                        String ossUrl = "";
                        ossUrl = OSSPublicUploadInterface.ftpAttachment(f, "doctrans", "docx2html");
                        if (ossUrl != null && !StringUtils.isEmpty(ossUrl)) {
                            JSONObject jsonObject = JSONObject.parseObject(ossUrl);
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data != null) {
                                ossUrl = data.getString("path");
                                //替换图片链接
                                String imgUrl = (f + "").replaceAll("\\\\", "\\/").replace("/data/", "");
                                htmlResult = htmlResult.replace(imgUrl, ossUrl);
                            }
                        }
                        f.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //删除本地文件
            if (htmlFile != null) {
                htmlFile.delete();
            }
            if (file != null) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return htmlResult;
    }
}
