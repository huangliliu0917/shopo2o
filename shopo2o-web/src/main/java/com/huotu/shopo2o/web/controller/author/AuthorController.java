package com.huotu.shopo2o.web.controller.author;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hxh on 2017-09-13.
 */
@Controller
public class AuthorController {
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            @AuthenticationPrincipal MallCustomer mallCustomer,
            Model model
    ) {
        model.addAttribute("mallCustomer",mallCustomer);
        return "home";
    }


    /**
     * 放开权限，所有角色均可修改自己的登录密码
     *
     * @return
     */
    @RequestMapping(value = "showModifyPwd")
    public String showModifyPwd() {
        return "modifyPwd";
    }

    /**
     * 放开权限，所有用户均可以修改自己的登录密码
     *
     * @param oldPwd
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/modifyPwd", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult modifyPwd(@AuthenticationPrincipal UserDetails user, String oldPwd, String password) throws
            Exception {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(oldPwd)) {
            return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
        password = passwordEncoder.encode(password);//32位MD5加密
        //校验旧密码
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return new ApiResult("密码错误!");
        }
        // TODO: 2017-09-13 修改密码
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

}
