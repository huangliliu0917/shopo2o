package com.huotu.shopo2o.web.controller.goods;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.good.HbmBrand;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import com.huotu.shopo2o.service.entity.good.MallGood;
import com.huotu.shopo2o.service.entity.good.MallProduct;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import com.huotu.shopo2o.service.repository.good.HbmBrandRepository;
import com.huotu.shopo2o.service.repository.good.HbmGoodsTypeRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierGoodsRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierProductsRepository;
import com.huotu.shopo2o.service.repository.good.MallProductRepository;
import com.huotu.shopo2o.service.service.goods.HbmBrandService;
import com.huotu.shopo2o.service.service.goods.HbmGoodsTypeService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierGoodsService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierProductsService;
import com.huotu.shopo2o.web.CommonTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by xyr on 2017/10/11.
 */
@Transactional
public class StoreManagerControllerTest extends CommonTestBase {

    @Autowired
    private HbmSupplierGoodsRepository goodsRepository;
    @Autowired
    private HbmSupplierProductsService productsService;
    @Autowired
    private HbmSupplierProductsRepository productsRepository;
    @Autowired
    private MallProductRepository mallProductRepository;

    private static String BASE_URL = "/store";
    private MallCustomer user;
    private String userName;
    private String passWord;

    /**
     * 用于测试的商品类目
     *
     * @throws Exception
     */
    private HbmGoodsType mockGoodsTypeA = null;
    private HbmGoodsType mockGoodsTypeB = null;
    private HbmGoodsType mockSubGoodsTypeA = null;
    private HbmGoodsType mockSubGoodsTypeB = null;
    private int firstTypeNum = 0;

    /**
     * 用于测试的品牌
     */
    private HbmBrand hbmBrandA = null;
    private HbmBrand hbmBrandB = null;
    private HbmBrand hbmBrandC = null;
    private HbmBrand hbmBrandD = null;
    /**
     * 用于测试的商品
     */
    private HbmSupplierGoods hbmSupplierGoodsA = null;
    private HbmSupplierGoods hbmSupplierGoodsB = null;
    private HbmSupplierProducts hbmSupplierProductsA = null;
    private HbmSupplierProducts hbmSupplierProductsB = null;
    private HbmSupplierProducts hbmSupplierProductsC = null;
    private HbmSupplierProducts hbmSupplierProductsD = null;
    private MallGood mallGoodA = null;
    private MallProduct mallProductA = null;
    private MallProduct mallProductB = null;

    @Before
    public void setUp() throws Exception {
        //添加门店商户
        //大平台商户
        MallCustomer mallCustomer = new MallCustomer();
        mallCustomer.setNickName(UUID.randomUUID().toString());
        mallCustomer.setUsername(UUID.randomUUID().toString());
        mallCustomer.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        mallCustomer.setCustomerType(CustomerTypeEnum.HUOBAN_MALL);
        mallCustomer = mallCustomerRepository.saveAndFlush(mallCustomer);
        //门店商户
        userName = UUID.randomUUID().toString();
        passWord = UUID.randomUUID().toString();
        MallCustomer storeCustomer = new MallCustomer();
        storeCustomer.setNickName(UUID.randomUUID().toString());
        storeCustomer.setUsername(userName);
        storeCustomer.setPassword(passwordEncoder.encode(passWord));
        storeCustomer.setCustomerType(CustomerTypeEnum.STORE);
        storeCustomer = mallCustomerRepository.saveAndFlush(storeCustomer);
        //添加门店
        Store store = new Store();
        store.setId(storeCustomer.getCustomerId());
        store.setCustomer(mallCustomer);
        store.setDeleted(false);
        store.setDisabled(false);
        storeCustomer.setStore(store);
        user = mallCustomerRepository.saveAndFlush(storeCustomer);

        //写入品牌
        hbmBrandA = mockHbmBrand();
        hbmBrandB = mockHbmBrand();
        hbmBrandC = mockHbmBrand();
        hbmBrandD = mockHbmBrand();

        //创建类目数据
        //当前数据库中一级目录个数
        mockGoodsTypeA = mockHbmGoodsType(true, "200");
        mockGoodsTypeB = mockHbmGoodsType(true, "100");

        //mockGoodsTypeA下面2个子目录
        mockSubGoodsTypeA = mockHbmGoodsType(false, "2001");
        mockSubGoodsTypeB = mockHbmGoodsType(false, "2002");
        mockHbmTypeBrand(mockSubGoodsTypeA.getTypeId(), hbmBrandA.getBrandId());
        mockHbmTypeBrand(mockSubGoodsTypeA.getTypeId(), hbmBrandB.getBrandId());
        mockHbmTypeBrand(mockSubGoodsTypeA.getTypeId(), hbmBrandC.getBrandId());
        mockHbmTypeBrand(mockSubGoodsTypeA.getTypeId(), hbmBrandD.getBrandId());

        //写入商品
        hbmSupplierGoodsA = new HbmSupplierGoods();
        hbmSupplierGoodsA.setType(mockSubGoodsTypeA);
        hbmSupplierGoodsA.setStoreId(user.getCustomerId());
        hbmSupplierGoodsA.setBn("2016010721221");
        hbmSupplierGoodsA.setName("GoodsA");
        hbmSupplierGoodsA.setDisabled(false);
        hbmSupplierGoodsA.setStore(50);
        hbmSupplierGoodsA.setPdtDesc("[{\"ProductId\":14,\"Desc\":\"红色,S\"},{\"ProductId\":15,\"Desc\":\"红色,M\"}]");
        hbmSupplierGoodsA.setLastModify(new Date());
        hbmSupplierGoodsA.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.CHECKED);
        hbmSupplierGoodsA.setBrand(hbmBrandA);
        hbmSupplierGoodsA.setThumbnailPic("thumbnailPic");
        hbmSupplierGoodsA.setSmallPic("smallPic");
        hbmSupplierGoodsA.setBigPic("bigPic");
        hbmSupplierGoodsA.setBrief("brief");
        hbmSupplierGoodsA.setIntro("intro");
        hbmSupplierGoodsA.setMktPrice(25);
        hbmSupplierGoodsA.setCost(26);
        hbmSupplierGoodsA.setPrice(30);
        hbmSupplierGoodsA.setMarketable(true);
        hbmSupplierGoodsA.setWeight(5);
        hbmSupplierGoodsA.setUnit("unit");
        hbmSupplierGoodsA.setSpec("spec");
        hbmSupplierGoodsA.setSpecDesc("specDesc");
        hbmSupplierGoodsA.setNotifyNum(1);
        hbmSupplierGoodsA.setCreateTime(new Date());
        hbmSupplierGoodsA = goodsRepository.saveAndFlush(hbmSupplierGoodsA);

        hbmSupplierGoodsB = new HbmSupplierGoods();
        hbmSupplierGoodsB.setType(mockSubGoodsTypeB);
        hbmSupplierGoodsB.setStoreId(user.getCustomerId());
        hbmSupplierGoodsB.setBn("2016010721223");
        hbmSupplierGoodsB.setName("GoodsB");
        hbmSupplierGoodsB.setDisabled(false);
        hbmSupplierGoodsB.setStore(250);
        hbmSupplierGoodsB.setPdtDesc("[{\"ProductId\":14,\"Desc\":\"红色,S\"},{\"ProductId\":15,\"Desc\":\"红色,M\"}]");
        hbmSupplierGoodsB.setLastModify(new Date());
        hbmSupplierGoodsB.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.CHECKED);
        hbmSupplierGoodsB.setBrand(hbmBrandB);
        hbmSupplierGoodsB.setThumbnailPic("thumbnailPic");
        hbmSupplierGoodsB.setSmallPic("smallPic");
        hbmSupplierGoodsB.setBigPic("bigPic");
        hbmSupplierGoodsB.setBrief("brief");
        hbmSupplierGoodsB.setIntro("intro");
        hbmSupplierGoodsB.setMktPrice(25);
        hbmSupplierGoodsB.setCost(26);
        hbmSupplierGoodsB.setPrice(30);
        hbmSupplierGoodsB.setMarketable(false);
        hbmSupplierGoodsB.setWeight(5);
        hbmSupplierGoodsB.setUnit("unit");
        hbmSupplierGoodsB.setSpec("spec");
        hbmSupplierGoodsB.setSpecDesc("specDesc");
        hbmSupplierGoodsB.setNotifyNum(1);
        hbmSupplierGoodsB.setCreateTime(new Date());
        hbmSupplierGoodsB = goodsRepository.saveAndFlush(hbmSupplierGoodsB);

        //写入货品
        hbmSupplierProductsA = new HbmSupplierProducts();
        hbmSupplierProductsA.setSupplierGoodsId(hbmSupplierGoodsA.getSupplierGoodsId());
        hbmSupplierProductsA.setBn("2016010721221-1");
        hbmSupplierProductsA.setStore(22);
        hbmSupplierProductsA.setName("productA");
        hbmSupplierProductsA.setProps("[{\"SpecId\":1,\"SpecValueId\":1,\"SpecValue\":\"红色\"},{\"SpecId\":2,\"SpecValueId\":8,\"SpecValue\":\"S\"}]");
        hbmSupplierGoodsB.setLastModify(new Date());
        hbmSupplierProductsA.setBarcode("barcode");
        hbmSupplierProductsA.setTitle("title");
        hbmSupplierProductsA.setMinPrice(12);
        hbmSupplierProductsA.setMaxPrice(12);
        hbmSupplierProductsA.setCost(12);
        hbmSupplierProductsA.setMktPrice(12);
        hbmSupplierProductsA.setWeight(12);
        hbmSupplierProductsA.setUnit("unit");
        hbmSupplierProductsA.setFreez(0);
        hbmSupplierProductsA.setPdtDesc("pdtDesc");
        hbmSupplierProductsA.setMarketable(1);
        hbmSupplierProductsA.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT);
        hbmSupplierProductsA = productsRepository.saveAndFlush(hbmSupplierProductsA);

        hbmSupplierProductsB = new HbmSupplierProducts();
        hbmSupplierProductsB.setSupplierGoodsId(hbmSupplierGoodsA.getSupplierGoodsId());
        hbmSupplierProductsB.setBn("2016010721221-2");
        hbmSupplierProductsB.setStore(28);
        hbmSupplierProductsB.setName("productB");
        hbmSupplierProductsB.setProps("[{\"SpecId\":1,\"SpecValueId\":1,\"SpecValue\":\"红色\"},{\"SpecId\":2,\"SpecValueId\":8,\"SpecValue\":\"S\"}]");
        hbmSupplierProductsB.setLastModify(new Date());
        hbmSupplierProductsB.setBarcode("barcode");
        hbmSupplierProductsB.setTitle("title");
        hbmSupplierProductsB.setMinPrice(12);
        hbmSupplierProductsB.setMaxPrice(12);
        hbmSupplierProductsB.setCost(12);
        hbmSupplierProductsB.setMktPrice(12);
        hbmSupplierProductsB.setWeight(12);
        hbmSupplierProductsB.setUnit("unit");
        hbmSupplierProductsB.setFreez(0);
        hbmSupplierProductsB.setPdtDesc("pdtDesc");
        hbmSupplierProductsB.setMarketable(1);
        hbmSupplierProductsB.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT);
        hbmSupplierProductsB = productsRepository.saveAndFlush(hbmSupplierProductsB);

        hbmSupplierProductsC = new HbmSupplierProducts();
        hbmSupplierProductsC.setSupplierGoodsId(hbmSupplierGoodsB.getSupplierGoodsId());
        hbmSupplierProductsC.setBn("2016010721223-1");
        hbmSupplierProductsC.setStore(100);
        hbmSupplierProductsC.setName("productC");
        hbmSupplierProductsC.setProps("[{\"SpecId\":1,\"SpecValueId\":1,\"SpecValue\":\"红色\"},{\"SpecId\":2,\"SpecValueId\":8,\"SpecValue\":\"S\"}]");
        hbmSupplierProductsC.setLastModify(new Date());
        hbmSupplierProductsC.setBarcode("barcode");
        hbmSupplierProductsC.setTitle("title");
        hbmSupplierProductsC.setMinPrice(12);
        hbmSupplierProductsC.setMaxPrice(12);
        hbmSupplierProductsC.setCost(12);
        hbmSupplierProductsC.setMktPrice(12);
        hbmSupplierProductsC.setWeight(12);
        hbmSupplierProductsC.setUnit("unit");
        hbmSupplierProductsC.setFreez(0);
        hbmSupplierProductsC.setPdtDesc("pdtDesc");
        hbmSupplierProductsC.setMarketable(1);
        hbmSupplierProductsC.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT);
        hbmSupplierProductsC = productsRepository.saveAndFlush(hbmSupplierProductsC);

        hbmSupplierProductsD = new HbmSupplierProducts();
        hbmSupplierProductsD.setSupplierGoodsId(hbmSupplierGoodsB.getSupplierGoodsId());
        hbmSupplierProductsD.setBn("2016010721223-2");
        hbmSupplierProductsD.setStore(150);
        hbmSupplierProductsD.setName("productD");
        hbmSupplierProductsD.setProps("[{\"SpecId\":1,\"SpecValueId\":1,\"SpecValue\":\"红色\"},{\"SpecId\":2,\"SpecValueId\":8,\"SpecValue\":\"S\"}]");
        hbmSupplierProductsD.setLastModify(new Date());
        hbmSupplierProductsD.setBarcode("barcode");
        hbmSupplierProductsD.setTitle("title");
        hbmSupplierProductsD.setMinPrice(12);
        hbmSupplierProductsD.setMaxPrice(12);
        hbmSupplierProductsD.setCost(12);
        hbmSupplierProductsD.setMktPrice(12);
        hbmSupplierProductsD.setWeight(12);
        hbmSupplierProductsD.setUnit("unit");
        hbmSupplierProductsD.setFreez(0);
        hbmSupplierProductsD.setPdtDesc("pdtDesc");
        hbmSupplierProductsD.setMarketable(1);
        hbmSupplierProductsD.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT);
        hbmSupplierProductsD = productsRepository.saveAndFlush(hbmSupplierProductsD);

        //写入上架商品
        mallProductA = mockMallProduct(hbmSupplierProductsA);
        mallProductB = mockMallProduct(hbmSupplierProductsB);
        List<MallProduct> productList = Lists.newArrayList();
        productList.add(mallProductA);
        productList.add(mallProductB);
        mallGoodA = mockMallGood(hbmSupplierGoodsA, productList);
        mallProductA.setGood(mallGoodA);
        mallProductB.setGood(mallGoodA);
        mallProductRepository.saveAndFlush(mallProductA);
        mallProductRepository.saveAndFlush(mallProductB);

        hbmSupplierGoodsA.setMallGoodsId(mallGoodA.getGoodId());
        hbmSupplierGoodsA = goodsRepository.saveAndFlush(hbmSupplierGoodsA);
        hbmSupplierProductsA.setMallProductId(mallProductA.getProductId());
        hbmSupplierProductsA = productsRepository.saveAndFlush(hbmSupplierProductsA);
        hbmSupplierProductsB.setMallProductId(mallProductB.getProductId());
        hbmSupplierProductsB = productsRepository.saveAndFlush(hbmSupplierProductsB);
    }

    @Test
    public void showProductsStore() throws Exception {
        MockHttpSession session = loginAs(userName, passWord);

        //当goodsId为空时，获取到的值应该为null
        MvcResult result1 = mockMvc.perform(post(BASE_URL + "/showProductsStore").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/editStore"))
                .andReturn();
        Assert.assertNull(result1.getModelAndView().getModel().get("hbmSupplierGoods"));
        Assert.assertNull(result1.getModelAndView().getModel().get("productList"));
        Assert.assertNull(result1.getModelAndView().getModel().get("specList"));

        //当goodsId不存在时，返回的值也都为null
        MvcResult result2 = mockMvc.perform(post(BASE_URL + "/showProductsStore")
                .session(session)
                .param("goodsId", String.valueOf(hbmSupplierGoodsA.getSupplierGoodsId() + 100)))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/editStore"))
                .andReturn();
        Assert.assertNull(result1.getModelAndView().getModel().get("hbmSupplierGoods"));
        Assert.assertNull(result1.getModelAndView().getModel().get("productList"));
        Assert.assertNull(result1.getModelAndView().getModel().get("specList"));

        //当正确获取时
        MvcResult result3 = mockMvc.perform(post(BASE_URL + "/showProductsStore")
                .session(session)
                .param("goodsId", String.valueOf(hbmSupplierGoodsA.getSupplierGoodsId())))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/editStore"))
                .andReturn();
        List<HbmSupplierProducts> exceptProducts = productsService.getProductListByGoodId(hbmSupplierGoodsA.getSupplierGoodsId());
        List<HbmSupplierProducts> realProducts = (List<HbmSupplierProducts>) result3.getModelAndView().getModel().get("productList");
        HbmSupplierGoods realGoods = (HbmSupplierGoods) result3.getModelAndView().getModel().get("hbmSupplierGoods");
        Assert.assertEquals(hbmSupplierGoodsA.getSupplierGoodsId(), realGoods.getSupplierGoodsId());
        Assert.assertEquals(exceptProducts.get(0), realProducts.get(0));
        Assert.assertEquals(exceptProducts.get(1), realProducts.get(1));
    }

    @Test
    public void modifyStore() throws Exception {
        MockHttpSession session = loginAs(userName, passWord);

        //当goodsId/productId/productStore有其中一个或多个为null时
        MvcResult result1 = mockMvc.perform(post(BASE_URL + "/storeManager").session(session))
                .andExpect(status().isOk())
                .andReturn();
        String content1 = new String(result1.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj1 = JSONObject.parseObject(content1);
        Assert.assertEquals("没有传输数据", obj1.get("msg"));

        //当goodsId不存在
        String[] productId = {String.valueOf(hbmSupplierProductsA.getSupplierProductId()), String.valueOf(hbmSupplierProductsB.getSupplierProductId())};
        String[] productStore = {"101", "102"};
        MvcResult result2 = mockMvc.perform(post(BASE_URL + "/storeManager")
                .session(session)
                .param("supplierGoodsId", String.valueOf(hbmSupplierGoodsA.getSupplierGoodsId() + 100))
                .param("productId", productId)
                .param("productStore", productStore))
                .andExpect(status().isOk())
                .andReturn();
        String content2 = new String(result2.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj2 = JSONObject.parseObject(content2);
        Assert.assertEquals("商品编号错误！", obj2.get("msg"));

        //当productId集合和productStore集合数量不一致
        String[] falseProductStore = {"101", "102", "103"};
        MvcResult result3 = mockMvc.perform(post(BASE_URL + "/storeManager")
                .session(session)
                .param("supplierGoodsId", String.valueOf(hbmSupplierGoodsA.getSupplierGoodsId()))
                .param("productId", productId)
                .param("productStore", falseProductStore))
                .andExpect(status().isOk())
                .andReturn();
        String content3 = new String(result3.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj3 = JSONObject.parseObject(content3);
        Assert.assertEquals("数据解析失败", obj3.get("msg"));

        //正确传输数据的情况
        MvcResult result4 = mockMvc.perform(post(BASE_URL + "/storeManager")
                .session(session)
                .param("supplierGoodsId", String.valueOf(hbmSupplierGoodsA.getSupplierGoodsId()))
                .param("productId", productId)
                .param("productStore", productStore))
                .andExpect(status().isOk())
                .andReturn();
        String content4 = new String(result4.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj4 = JSONObject.parseObject(content4);
        Assert.assertEquals("请求成功", obj4.get("msg"));

        List<HbmSupplierProducts> nowProducts = productsRepository.findBySupplierGoodsId(hbmSupplierGoodsA.getSupplierGoodsId());
        nowProducts.forEach(np -> {
            for (int i = 0; i < productId.length; i ++) {
                if (np.getSupplierProductId() == Integer.parseInt(productId[i])) {
                    Assert.assertEquals(Integer.parseInt(productStore[i]), np.getStore());
                }
            }
        });
    }

}