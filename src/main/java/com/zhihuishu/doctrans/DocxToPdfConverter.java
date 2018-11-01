package com.zhihuishu.doctrans;


import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DocxToPdfConverter {

    public static void main (String[] args) throws Exception {
        XWPFDocument document = new XWPFDocument(new FileInputStream("D:\\用户目录\\下载\\公式.docx"));
        PdfOptions options = PdfOptions.create();
        PdfConverter.getInstance().convert(document, new FileOutputStream("D:\\用户目录\\下载\\1.pdf"), options);

    }
}
