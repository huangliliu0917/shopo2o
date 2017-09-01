package com.huotu.shopo2o.web.controller;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by hxh on 2017-08-31.
 */
@Controller
public class TestSecurityController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = {"", "/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping("/loginFailed")
    public String loginFailed(RedirectAttributes attributes) {
        attributes.addFlashAttribute("errMsg", "账号或密码错误!");
        attributes.addFlashAttribute("loginFailed", true);
        return "redirect:index";
    }

    @RequestMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal MallCustomer mallCustomer, Model model) {
        model.addAttribute("mallCustomer", mallCustomer);
        return "home";
    }

    @RequestMapping("/checkLogin")
    @ResponseBody
    public ApiResult checkLogin(String userName, String passWord) {
        boolean flag = loginService.checkLogin(userName, passWord);
        if (flag) {
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "账号或密码错误", null);
    }
}
