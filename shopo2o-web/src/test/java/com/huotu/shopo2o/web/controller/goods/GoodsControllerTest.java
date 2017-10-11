package com.huotu.shopo2o.web.controller.goods;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.service.goods.HbmGoodsTypeService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierGoodsService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierProductsService;
import com.huotu.shopo2o.web.CommonTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by xyr on 2017/10/11.
 */
@Transactional
public class GoodsControllerTest extends CommonTestBase {

    @Autowired
    private HbmGoodsTypeService typeService;
    @Autowired
    private HbmSupplierProductsService productsService;
    @Autowired
    private HbmSupplierGoodsService goodsService;

    private static String BASE_URL = "/goods";
    private MallCustomer user;
    private String userName;
    private String passWord;

    @Before
    private void setUp() {

    }

    @Test
    public void showType() throws Exception {

    }

    @Test
    public void showGoodsList() throws Exception {

    }

    @Test
    public void updateGoodStatus() throws Exception {

    }

}