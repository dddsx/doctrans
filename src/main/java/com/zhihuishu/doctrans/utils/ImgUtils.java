package com.zhihuishu.doctrans.utils;

//import net.arnx.wmf2svg.gdi.svg.SvgGdi;
//import net.arnx.wmf2svg.gdi.wmf.WmfParser;
//import org.apache.batik.transcoder.TranscoderException;
//import org.apache.batik.transcoder.TranscoderInput;
//import org.apache.batik.transcoder.TranscoderOutput;
//import org.apache.batik.transcoder.image.ImageTranscoder;
//import org.apache.batik.transcoder.image.JPEGTranscoder;
//import org.apache.batik.transcoder.image.PNGTranscoder;
//import org.w3c.dom.Document;
//
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import java.io.*;
//import java.nio.file.Paths;

public class ImgUtils {

//    public static void svgToJpg(File srcFile, File destFile) {
//        FileOutputStream jpgOut = null;
//        FileInputStream svgStream = null;
//        ByteArrayOutputStream svgOut = null;
//        ByteArrayInputStream svgInputStream = null;
//        ByteArrayOutputStream jpg = null;
//        File svg=null;
//        try {
//            // 获取到svg文件
//            svgStream = new FileInputStream(srcFile);
//            svgOut = new ByteArrayOutputStream();
//            // 获取到svg的stream
//            int noOfByteRead = 0;
//            while ((noOfByteRead = svgStream.read()) != -1) {
//                svgOut.write(noOfByteRead);
//            }
//            ImageTranscoder it = new PNGTranscoder();
//            it.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(0.9f));
//            it.addTranscodingHint(ImageTranscoder.KEY_WIDTH, new Float(100));
//            jpg = new ByteArrayOutputStream();
//            svgInputStream = new ByteArrayInputStream(svgOut.toByteArray());
//            it.transcode(new TranscoderInput(svgInputStream),
//                    new TranscoderOutput(jpg));
//            jpgOut = new FileOutputStream(destFile);
//            jpgOut.write(jpg.toByteArray());
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (svgInputStream != null) {
//                    svgInputStream.close();
//                }
//                if (jpg != null) {
//                    jpg.close();
//                }
//                if (svgStream != null) {
//                    svgStream.close();
//                }
//                if (svgOut != null) {
//                    svgOut.close();
//                }
//                if (jpgOut != null) {
//                    jpgOut.flush();
//                    jpgOut.close();
//                }
//                if(svg!=null&&svg.exists()){
//                    svg.delete();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void wmfToSvg(File srcFile, File destFile){
//        try {
//            InputStream in = new FileInputStream(srcFile);
//            WmfParser parser = new WmfParser();
//            SvgGdi gdi = new SvgGdi(false);
//            parser.parse(in, gdi);
//
//            Document doc = gdi.getDocument();
//            OutputStream out = new FileOutputStream(destFile);
//            output(doc, out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private static void output(Document doc, OutputStream out) throws Exception {
//        TransformerFactory factory = TransformerFactory.newInstance();
//        Transformer transformer = factory.newTransformer();
//        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
//                "-//W3C//DTD SVG 1.0//EN");
//        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
//                "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
//        transformer.transform(new DOMSource(doc), new StreamResult(out));
//        out.flush();
//        out.close();
//    }
//
//    public static void main(String[] args) throws IOException, TranscoderException {
////        wmfToSvg(new File("D:\\data\\image\\word\\media\\image1.wmf"),
////                new File("D:\\data\\image\\word\\media\\image1.svg"));
//        //svgToJpg(new File("D:\\data\\image\\word\\media\\image1.svg"), new File("D:\\data\\image\\word\\media\\image1.jpg"));
//
//
//        String svg_URI_input = Paths.get("D:\\data\\image\\word\\media\\image1.svg").toUri().toURL().toString();
//        TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
//        //Step-2: Define OutputStream to PNG Image and attach to TranscoderOutput
//        OutputStream png_ostream = new FileOutputStream("D:\\data\\image\\word\\media\\chessboard.png");
//        TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
//        // Step-3: Create PNGTranscoder and define hints if required
//        PNGTranscoder my_converter = new PNGTranscoder();
//        // Step-4: Convert and Write output
//        my_converter.transcode(input_svg_image, output_png_image);
//        // Step 5- close / flush Output Stream
//        png_ostream.flush();
//        png_ostream.close();
//    }
}
