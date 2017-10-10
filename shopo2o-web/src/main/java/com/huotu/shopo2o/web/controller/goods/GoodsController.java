package com.huotu.shopo2o.web.controller.goods;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.service.goods.HbmGoodsTypeService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
@Controller
@RequestMapping(value = "/good")
public class GoodsController {

    @Autowired
    private HbmGoodsTypeService hbmGoodsTypeService;

    /**
     * 新增商品时，显示选择类型界面中的一级类型,并根据tOrder排序
     * 显示用户最近10个新增的商品类型
     *
     * @param customer 用户登录
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showType")
    public ModelAndView showType(@LoginUser MallCustomer customer) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("goods/goodType");
        //获取一级标准LIST 第一级的标准父类目ID为 "0"
        List<HbmGoodsType> typeList = hbmGoodsTypeService.getGoodsTypeByParentId("0");
        //获取该用户最近10个新增的商品类型
        if (customer != null && customer.getCustomerId() != 0) {
            List<HbmGoodsType> lastUsedType = hbmGoodsTypeService.getGoodsTypeLastUsed(customer.getCustomerId());
            modelAndView.addObject("lastUsedType", lastUsedType);
        }
        modelAndView.addObject("typeList", typeList);
        return modelAndView;
    }


}
