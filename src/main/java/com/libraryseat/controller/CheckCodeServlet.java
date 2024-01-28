package com.libraryseat.controller;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Random;

/**
 * 验证码产生
 */
// 不带body,故使用get
@WebServlet("/checkcode")
public class CheckCodeServlet extends HttpServlet {
    private final Random random = new Random();
    private static final char[] BASE = "0123456789ABCDEFGHIabcdefghi".toCharArray(); //不定义为字符串，方便使用
    private static final int WIDTH = 90, HEIGHT = 30;
    private static final Color[] COLORS = {
            Color.green, Color.white, Color.yellow, new Color(208,208,208), Color.cyan,Color.orange
    };

    private Color getRandomColor() {
        int index = random.nextInt(COLORS.length);
        return COLORS[index];
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//服务器通知浏览器不要缓存
        response.setHeader("pragma", "no-cache");
        response.setHeader("cache-control", "no-cache");
        response.setHeader("Content-type", "image/jpeg");
//产生随机验证码
        Map.Entry<String, String> checkCode = getArithmeticCheckCode();
        String expression = checkCode.getKey();
//将验证码放入session中
        request.getSession().setAttribute("CHECKCODE_SERVER", checkCode.getValue());
        BufferedImage image = generateImage(expression);
//将内存中的图片输出到浏览器
        ImageIO.write(image, "JPG", response.getOutputStream());
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405);
    }

    private String getSimpleCheckCode() {
        StringBuffer sbuffer = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(BASE.length);
            sbuffer.append(BASE[index]);
        }
        return sbuffer.toString();
    }
    /**算术验证码，生成加减法算式*/
    private Map.Entry<String, String> getArithmeticCheckCode() {
        int first = random.nextInt(60),second = random.nextInt(60);
        boolean subt = random.nextBoolean();
        if (subt) {
            if (first < second)
                return new SimpleImmutableEntry<>(String.format("%d-%d=?",second,first),Integer.toString(second-first));
            return new SimpleImmutableEntry<>(String.format("%d-%d=?",first,second),Integer.toString(first-second));
        }
        return new SimpleImmutableEntry<>(String.format("%d+%d=?",first,second),Integer.toString(first+second));
    }
    private BufferedImage generateImage(String text) {
        //在内存中创建一个长80，宽30的图片，默认黑色背景
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
//获取画笔
        Graphics g = image.getGraphics();
//设置画笔颜色为灰色
        g.setColor(Color.gray);
//填充图片&添加随机点干扰
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for(int i = 0; i < 20; i++) {
            g.setColor(getRandomColor());
            g.drawString(".",random.nextInt(WIDTH),random.nextInt(HEIGHT));
        }
//设置画笔颜色
        g.setColor(getRandomColor());
//设置字体
        g.setFont(new Font("宋体", Font.BOLD, 20));
//向图片上写入验证码
        for (int i = 0; i < text.length(); i++) {
            g.drawString(text.substring(i,i+1), i*12+10, HEIGHT/2+5+random.nextInt(5));
        }
//        g.drawString(text, 10, 25);
//添加随机曲线干扰
        g.setColor(getRandomColor());
        g.drawArc(0, 0, WIDTH, HEIGHT, random.nextInt(180), random.nextInt(180));
        return image;
    }
}
