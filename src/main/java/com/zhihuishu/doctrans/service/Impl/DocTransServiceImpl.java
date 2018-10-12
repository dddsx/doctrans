package com.zhihuishu.doctrans.service.Impl;

import com.able.base.ftp.oss.OSSPublicUploadInterface;
import com.alibaba.fastjson.JSONObject;
import com.zhihuishu.doctrans.service.DocTransService;
import com.zhihuishu.doctrans.utils.CustomXWPFDocument;
import com.zhihuishu.doctrans.utils.MyFileUtil;
import com.zhihuishu.doctrans.utils.XWPFUtils;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
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
        String str = "";
        String sourceFileName = "";
        File file = null;
        //通过url下载图片
        if (url != null && !StringUtils.isEmpty(url)) {
            file = MyFileUtil.downloadFile(url, DATA_DOMNLOAD);
        }
        //文件不存在
        if ((file == null || !file.exists()) && multipartFile != null && multipartFile.getSize() > 0) {
            try {
                file = MyFileUtil.inputStreamToFile(multipartFile, request, DATA_DOMNLOAD);
            } catch (Exception e) {
                e.printStackTrace();
                file = null;
            }

        }
        if (file == null) {
            return "";
        }

        sourceFileName = DATA_DOMNLOAD + file.getName();
        String filename = file.getName();
        if (!StringUtils.isEmpty(filename)) {
            filename = filename.split("\\.")[0];
        }
        File filehtmldir = new File(DATA_HTML);
        if (filehtmldir.isDirectory()) {

        } else {
            try {
                filehtmldir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filehtmldir = new File(DATA_IMAGE);
        if (filehtmldir.isDirectory()) {

        } else {
            try {
                filehtmldir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filehtmldir = new File(DATA_IMAGE + "word/");
        if (filehtmldir.isDirectory()) {

        } else {
            try {
                filehtmldir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filehtmldir = new File(DATA_IMAGE_SOURCE);
        if (filehtmldir.isDirectory()) {

        } else {
            try {
                filehtmldir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String targetFileName = DATA_HTML + filename + ".html";
        OutputStreamWriter outputStreamWriter = null;
        try {
            CustomXWPFDocument document = new CustomXWPFDocument(new FileInputStream(sourceFileName));

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
                    byte[] bytev = picture.getData();
                    // 输出图片到磁盘
                    FileOutputStream out = new FileOutputStream(
                            DATA_IMAGE_SOURCE + picture.getFileName());
                    out.write(bytev);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            XHTMLOptions options = XHTMLOptions.create();
            // 存放图片的文件夹
            options.setExtractor(new FileImageExtractor(new File(DATA_IMAGE)));
            // html中图片的路径
            options.URIResolver(new BasicURIResolver("image"));
            options.setFragment(true);
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(targetFileName), "utf-8");
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, outputStreamWriter, options);

            // 读取html并返回
            // 定义一个file对象，用来初始化FileReader
            File filehtml = new File(targetFileName);
            // 定义一个fileReader对象，用来初始化BufferedReader
            FileReader reader = new FileReader(filehtml);
            // new一个BufferedReader对象，将文件内容读取到缓存
            BufferedReader bReader = new BufferedReader(reader);
            // 定义一个字符串缓存，将字符串存放缓存中
            StringBuilder sb = new StringBuilder();
            String s = "";
            // 逐行读取文件内容，不读取换行符和末尾的空格
            while ((s = bReader.readLine()) != null) {
                // 将读取的字符串添加换行符后累加存放在缓存中
                sb.append(s).append("\n");
            }
            bReader.close();
            str = sb.toString();
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
                                str = str.replace(imgUrl, ossUrl);
                            }
                        }
                        f.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //删除本地文件
            if (filehtml != null) {
                filehtml.delete();
            }
            if (file != null) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }
}
