package com.huotu.shopo2o.web.controller.author;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.StoreMenu;
import com.huotu.shopo2o.service.entity.author.Operator;
import com.huotu.shopo2o.service.entity.config.SupBasicConfig;
import com.huotu.shopo2o.service.model.IndexStatistics;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.service.repository.author.OperatorRepository;
import com.huotu.shopo2o.service.service.config.SupBasicConfigService;
import com.huotu.shopo2o.service.service.statistics.IndexStatisticsService;
import com.huotu.shopo2o.service.service.user.StoreMenuService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
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

import java.util.List;

/**
 * Created by hxh on 2017-09-13.
 */
@Controller
public class AuthorController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MallCustomerRepository mallCustomerRepository;
    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private StoreMenuService storeMenuService;
    @Autowired
    private IndexStatisticsService indexStatisticsService;
    @Autowired
    private SupBasicConfigService supBasicConfigService;

    @RequestMapping(value = {"", "/", "/login"})
    public String login() {
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
    @RequestMapping(value = {"/home"})
    public String loginSuccess(
            @LoginUser MallCustomer mallCustomer,
            Model model
    ) {
        List<StoreMenu> storeMenus = storeMenuService.findPrimary();
        model.addAttribute("storMenus",storeMenus);
        model.addAttribute("mallCustomer", mallCustomer);
        SupBasicConfig config = supBasicConfigService.findByStoreId(mallCustomer.getCustomerId());
        if(config != null && !StringUtils.isEmpty(config.getLogoName())){
            model.addAttribute("logoName",config.getLogoName());
        }else{
            model.addAttribute("logoName","");
        }
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

    @RequestMapping(value = "/index")
    public String index(@LoginUser MallCustomer mallCustomer, Model model) {
        IndexStatistics indexStatistics = indexStatisticsService.orderStatistics(Integer.parseInt(String.valueOf(mallCustomer.getCustomerId())));
        model.addAttribute("indexStatistics", indexStatistics);
        return "index";
    }

    /**
     * 放开权限，所有用户均可以修改自己的登录密码
     *
     * @param user 这里要根据不同的类型去修改密码，所以使用 {@link org.springframework.security.core.annotation.AuthenticationPrincipal}
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
        //修改密码
        if(user instanceof MallCustomer){
            //如果是门店管理员
            ((MallCustomer) user).setPassword(password);
            mallCustomerRepository.saveAndFlush(((MallCustomer) user));
        }else if(user instanceof Operator){
            //如果是操作员
            ((Operator) user).setPassword(password);
            operatorRepository.saveAndFlush(((Operator) user));
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @RequestMapping("/leftMenu")
    public String leftMenu(
            String parentId,
            String activeMenuId,
            Model model
    ) {
        if (!StringUtils.isEmpty(parentId)) {
            List<StoreMenu> storeMenus = storeMenuService.findByParent(parentId, 0);

            model.addAttribute("storeMenus", storeMenus);
            StoreMenu activeMenu;
            if (StringUtils.isEmpty(activeMenuId)) {
                activeMenu = storeMenus.get(0).getChildren().get(0);
            } else {
                activeMenu = storeMenuService.findById(activeMenuId);
            }
            model.addAttribute("activeMenu", activeMenu);
        }
        model.addAttribute("parentId", parentId);
        return "left_menu";
    }
}
