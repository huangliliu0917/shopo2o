package com.huotu.shopo2o.web.controller;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by hxh on 2017-08-31.
 */
@Controller
public class TestSecurityController {


    @RequestMapping(value = {"", "/", "/login"})
    public String index() {
        return "login";
    }

    @RequestMapping("/loginFailed")
    public String loginFailed() {
        return "redirect:login";
    }
    /**
     * 登录验证通过后更新登录时间并跳转供应商后台首页
     *
     * @param mallCustomer
     * @return
     */
    @RequestMapping(value = {"/loginSuccess","/index"})
    public String loginSuccess(
            @LoginUser MallCustomer mallCustomer,
            Model model
    ) {
        model.addAttribute("mallCustomer",mallCustomer);
        return "home";
    }

}
