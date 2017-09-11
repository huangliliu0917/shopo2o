package com.huotu.shopo2o.web.controller.store;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.store.Store;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
@RequestMapping("/store")
@Controller
public class StoreController {
    @RequestMapping("/getStoreList")
    public String showCatList(@AuthenticationPrincipal MallCustomer customer, String option) {
        ModelAndView modelAndView = new ModelAndView();
        List<Store> catList;
        // TODO: 2017-09-11 店铺查询
        return "store/store_list";
    }

}
