package com.huotu.shopo2o.web.controller;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by hxh on 2017-08-31.
 */
@Controller
public class TestSecurityController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = {"", "/", "/login"})
    public String index() {
        return "index";
    }

    @RequestMapping("/loginFailed")
    public String loginFailed(RedirectAttributes attributes) {
        return "redirect:login";
    }

    @RequestMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal MallCustomer mallCustomer, Model model) {
        model.addAttribute("mallCustomer", mallCustomer);
        return "home";
    }
}