package com.huotu.shopo2o.web.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.httputil.HttpClientUtil;
import com.huotu.shopo2o.common.httputil.HttpResult;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import com.huotu.shopo2o.web.service.StaticResourceService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hxh on 2017-09-18.
 */
@Controller
public class UploadController {
    @Autowired
    private StaticResourceService resourceServer;

    @RequestMapping(value = "/supplier/upload", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> upLoad(
            @LoginUser MallCustomer customer,
            @RequestParam(value = "btnFile", required = false) MultipartFile files) {
        int result = 0;
        Map<Object, Object> responseData = new HashMap<Object, Object>();
        try {
            Date now = new Date();
            String fileName = files.getOriginalFilename();
            String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
            String path = StaticResourceService.IMG + customer.getCustomerId() + "/" + StringUtil.DateFormat(now, "yyyyMMdd") + "/"
                    + StringUtil.DateFormat(now, "yyyyMMddHHmmSS") + "." + prefix;
            URI uri = resourceServer.uploadResource(null, path, files.getInputStream());
            responseData.put("fileUrl", uri);
            responseData.put("fileUri", path);
            result = 1;
        } catch (Exception e) {
            responseData.put("msg", e.getMessage());
        }
        responseData.put("result", result);

        return responseData;
    }

    /**
     * 上传商品相关图片到 huobanmall
     * 宽度和高度一致
     *
     * @param customer
     * @param files
     * @return
     */
    @RequestMapping(value = "/supplier/uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> upLoadImage(
            @LoginUser MallCustomer customer,
            @RequestParam(value = "btnFile", required = false) MultipartFile files) {
        int result = 0;
        Map<Object, Object> responseData = new HashMap<Object, Object>();
        try {
            String fileName = files.getOriginalFilename();
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            BufferedImage image = ImageIO.read(files.getInputStream());
            BASE64Encoder encoder = new BASE64Encoder();
            String imgStr = encoder.encode(files.getBytes());
            boolean isSave = false;
            if (image != null) {
                int width = image.getWidth();
                int height = image.getHeight();
                Map<String, Object> map = new TreeMap<>();
                map.put("customid", customer.getCustomerId());
                map.put("base64Image", imgStr);
                map.put("size", width + "x" + height);
                map.put("extenName", prefix);

                HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/gallery/uploadPhoto", map);
                if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                    JSONObject obj = JSONObject.parseObject(httpResult.getHttpContent());
                    if (obj.getIntValue("code") == 200) {
                        String fileUri = obj.getString("data");
                        URI uri = resourceServer.getResource(StaticResourceService.huobanmallMode, fileUri);
                        responseData.put("fileUrl", uri);
                        responseData.put("fileUri", fileUri);
                        responseData.put("msg", "上传成功！");
                        responseData.put("code", 200);
                        //ueidtor上传图片所需参数
                        responseData.put("url", uri);
                        responseData.put("title", fileUri);
                        responseData.put("original", fileUri);
                        responseData.put("state", "SUCCESS");

                        result = 1;
                        isSave = true;
                    }
                } else {
                    result = 0;
                    isSave = false;
                }
            }
            if (!isSave) {
                responseData.put("code", 500);
                responseData.put("msg", "请上传正方形文件");
            }
        } catch (Exception e) {
            responseData.put("msg", e.getMessage());
        }
        responseData.put("result", result);

        return responseData;
    }

    /**
     * picture空间中中上传图片
     */
    @RequestMapping(value = "/supplier/picture/uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject uploadImage(
            @LoginUser MallCustomer customer, String images, String groupId) {
        Map<String, Object> map = new TreeMap<>();
        map.put("customerId", customer.getCustomerId());
        map.put("images", images);
        map.put("groupId", groupId);
        String result = "";
        HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/gallery/uploadImage", map);
        result = httpResult.getHttpContent();
        JSONObject obj = JSONObject.parseObject(httpResult.getHttpContent());
        return obj;
    }

    /**
     * 删除伙伴mall相关图片
     * todo
     *
     * @param customer
     * @param path
     * @return
     */
    @RequestMapping(value = "/supplier/removeImage", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> removeImage(@LoginUser MallCustomer customer, String... path) {
        int result = 0;
        Map<Object, Object> responseData = new HashMap<Object, Object>();
        try {
            if (path != null && path.length > 0) {
                for (String p : path) {
                    URI uri = resourceServer.getResource(resourceServer.huobanmallMode, SysConstant.HUOBANMALL_RESOURCE_HOST + p);
                    resourceServer.deleteResource(p);
                }
            }
            responseData.put("msg", "删除成功！");
            result = 1;
        } catch (IOException e) {
            e.printStackTrace();
            responseData.put("msg", e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            responseData.put("msg", e.getMessage());
        }
        responseData.put("result", result);
        return responseData;
    }
}
