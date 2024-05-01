package com.libraryseat.utils;

import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class ImageBase64Util {
    public static final int SIZE_LIMIT = 1 << 21; //2MB
    private static final String HEAD = "data:image/jpeg;base64,";

    /**
     * 将输入流解析为base64字符串
     * @param stream 输入流
     * @param height 输出的图像高度
     * @param width 输出的图像宽度
     * @throws IOException 输入的图片有误
     * @throws IllegalArgumentException 图片太大
     * @return 编码后的base64。采用jpeg格式
     */
    public static String encodeToBase64(InputStream stream,int height,int width) throws IOException,IllegalArgumentException {
        int size = stream.available();
        if (size > SIZE_LIMIT)
            throw new IllegalArgumentException("图片太大！");
        // 从InputStream中读取图片数据
        Image image = ImageIO.read(stream).getScaledInstance(width,height, Image.SCALE_FAST);
        BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        bufferedImage.getGraphics().drawImage(image,0,0,Color.WHITE,null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(width*height*3); //最多这么多字节
        ImageIO.write(bufferedImage, "JPEG", baos);
        byte[] imageBytes = baos.toByteArray();
        IOUtils.closeQuietly(baos); //实际不需要

        // 将图片数据编码为Base64字符串
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        // 添加前缀
        return HEAD + base64Image;
    }
    /**将base64字符串还原为图像数据.*/
    public static byte[] base64ToImageData(String base64) throws IllegalArgumentException {
        if (base64 == null||base64.length() == 0){
            return null;
        }
        if (!base64.startsWith(HEAD))
            throw new IllegalArgumentException("格式错误：只支持jpeg编码的base64数据！");
        return Base64.getDecoder().decode(base64.substring(HEAD.length()));
    }

    public static void writeImage(String base64, OutputStream stream) throws IOException,IllegalArgumentException {
        stream.write(base64ToImageData(base64));
    }
}
