package com.huotu.shopo2o.web.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Created by hxh on 2017-09-12.
 */
@Controller
@RequestMapping("/code")
public class CodeController {
    /**
     * 验证码个数
     */
    private int codeCount = 4;

    private String generateCheckOut(HttpServletRequest request) {
        int number = 0;
        char code;
        StringBuffer checkCode = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < codeCount; i++) {
            number = Math.abs(random.nextInt());
            if (number % 2 == 0) {
                code = (char) ('0' + (char) (number % 10));
            } else {
                code = (char) ('A' + (char) (number % 26));
            }
            checkCode.append(code);
        }
        // 将四位数字的验证码保存到Session中。
        HttpSession session = request.getSession();
        session.setAttribute("verifyCode", checkCode.toString().toLowerCase());
        return checkCode.toString();
    }

    @RequestMapping(value = "/verifyImage", produces = "image/*", method = RequestMethod.GET)
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String checkCode = generateCheckOut(request);
        if (StringUtils.isEmpty(checkCode)) {
            return;
        }
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage((int) Math.abs(checkCode.length() * 12.8), 52,
                BufferedImage.TYPE_INT_RGB);
        Graphics gd = buffImg.getGraphics();
        // 清空图片背景色
        Font font = new Font("Arial", (Font.BOLD | Font.ITALIC), 20);
        Color[] fontColor = {Color.BLACK, Color.RED, Color.PINK, Color.GREEN, Color.CYAN, Color.BLUE, Color.DARK_GRAY, Color.MAGENTA};  //定义 8 种颜色
        // 将图像填充为灰色
        gd.setColor(new Color(245,246,247));
        gd.fillRect(0, 0, buffImg.getWidth(), buffImg.getHeight());
        // 设置字体
        gd.setFont(font);
        for (int i = 0; i < codeCount; i++) {
            gd.setColor(fontColor[new Random().nextInt(8)]);
            gd.drawString(checkCode.substring(i,i+1), 2 + i * 12, 35);
        }
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        response.setContentType("image/jpeg");

        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos = response.getOutputStream();
        ImageIO.write(buffImg, "jpeg", sos);
        sos.close();
    }
}
