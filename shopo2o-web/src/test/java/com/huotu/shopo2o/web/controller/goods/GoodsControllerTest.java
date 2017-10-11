package com.huotu.shopo2o.web.controller.goods;

import com.alibaba.fastjson.JSONObject;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import com.huotu.shopo2o.service.model.HbmSupplierGoodsSearcher;
import com.huotu.shopo2o.service.repository.good.HbmGoodsTypeRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierGoodsRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierProductsRepository;
import com.huotu.shopo2o.service.service.goods.HbmGoodsTypeService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierGoodsService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierProductsService;
import com.huotu.shopo2o.web.CommonTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
    private HbmSupplierProductsRepository productsRepository;
    @Autowired
    private HbmSupplierGoodsService goodsService;
    @Autowired
    private HbmSupplierGoodsRepository goodsRepository;

    private static String BASE_URL = "/good";
    private MallCustomer user;
    private String userName;
    private String passWord;

    //羽绒服
    HbmGoodsType type = null;

    //用于测试的商品
    private HbmSupplierGoods mockSupplierGoods = null;
    private HbmSupplierProducts mockSupplierProducts = null;
    private HbmSupplierGoods mockSupplierGoodsSubmit = null;
    private HbmSupplierProducts mockSupplierProductsChecked = null;
    private HbmSupplierGoods mockSupplierGoodsChecked = null;
    private HbmSupplierProducts mockSupplierProductsSubmit = null;
    private HbmSupplierGoods mockSupplierGoodsDisabled = null;
    private HbmSupplierProducts mockSupplierProductsDisabled = null;
    private HbmSupplierGoods mockSupplierGoodsInsert = null;
    private HbmSupplierProducts mockSupplierProductsInsert = null;
    private int firstTypeNum = 0;

    @Before
    public void setUp() {
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

        //未提交审核的商品
        mockSupplierProducts = new HbmSupplierProducts();
        mockSupplierProducts.setBn("123");
        mockSupplierProducts.setMinPrice(80);
        mockSupplierProducts.setMaxPrice(80);
        mockSupplierProducts.setCost(50);
        mockSupplierProducts.setMktPrice(100);
        mockSupplierProducts.setName("商品名称");
        mockSupplierProducts.setWeight(100);
        mockSupplierProducts.setStore(100);
        mockSupplierProducts.setFreez(1000);
        mockSupplierProducts.setPdtDesc("M,栗色");
        mockSupplierProducts.setProps("[{\"SpecId\":\"2159\",\"SpecValueId\":\"9286\",\"SpecValue\":\"M\"},{\"SpecId\":\"2158\",\"SpecValueId\":\"9278\",\"SpecValue\":\"栗色\"}]");
        mockSupplierProducts.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT);
        mockSupplierProducts.setLastModify(new Date());
        mockSupplierProducts = productsRepository.saveAndFlush(mockSupplierProducts);

        type = typeService.getGoodsTypeByStandardTypeId("18");
        mockSupplierGoods = new HbmSupplierGoods();
        /*mockSupplierGoods.setType(type);
        mockSupplierGoods.setBrand(type.getBrandList().get(0));*/
        mockSupplierGoods.setStoreId(user.getCustomerId());
        mockSupplierGoods.setBrief("简介");
        mockSupplierGoods.setIntro("详细介绍");
        mockSupplierGoods.setMktPrice(mockSupplierProducts.getMktPrice());
        mockSupplierGoods.setCost(mockSupplierProducts.getCost());
        mockSupplierGoods.setPrice(mockSupplierProducts.getMinPrice());
        mockSupplierGoods.setBn(mockSupplierProducts.getBn());
        mockSupplierGoods.setName(mockSupplierProducts.getName());
        mockSupplierGoods.setWeight(mockSupplierProducts.getWeight());
        mockSupplierGoods.setStore(mockSupplierProducts.getStore());
        mockSupplierGoods.setThumbnailPic("image/invoice/6139/20160104/201601042007322.jpg");
        mockSupplierGoods.setSmallPic("image/invoice/6139/20160104/201601042007322.jpg");
        mockSupplierGoods.setBigPic("image/invoice/6139/20160104/201601042007322.jpg");
        mockSupplierGoods.setSpec("{\"2158\":\"颜色\",\"2159\":\"尺寸\"}");
        mockSupplierGoods.setPdtDesc("[{\"ProductId\":" + mockSupplierProducts.getSupplierProductId() + ",\"Desc\":\"M,栗色\"}]");
        mockSupplierGoods.setSpecDesc("[{\"SpecId\":2159,\"SpecValue\":\"M\",\"SpecValueId\":9286,\"SpecImage\":\"\",\"GoodsImageIds\":[]},{\"SpecId\":2158,\"SpecValue\":\"栗色\",\"SpecValueId\":9278,\"SpecImage\":\"\",\"GoodsImageIds\":[]}]");
        mockSupplierGoods.setCreateTime(new Date());
        mockSupplierGoods.setLastModify(new Date());
        mockSupplierGoods.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT);
        mockSupplierGoods = goodsRepository.saveAndFlush(mockSupplierGoods);
        mockSupplierProducts.setSupplierGoodsId(mockSupplierGoods.getSupplierGoodsId());
        mockSupplierProducts = productsRepository.saveAndFlush(mockSupplierProducts);

        //已提交审核商品
        mockSupplierProductsSubmit = new HbmSupplierProducts();
        mockSupplierProductsSubmit.setBn("124");
        mockSupplierProductsSubmit.setMinPrice(80);
        mockSupplierProductsSubmit.setMaxPrice(80);
        mockSupplierProductsSubmit.setCost(50);
        mockSupplierProductsSubmit.setMktPrice(100);
        mockSupplierProductsSubmit.setName("商品名称");
        mockSupplierProductsSubmit.setWeight(100);
        mockSupplierProductsSubmit.setStore(100);
        mockSupplierProductsSubmit.setPdtDesc("M,栗色");
        mockSupplierProductsSubmit.setProps("[{\"SpecId\":\"2159\",\"SpecValueId\":\"9286\",\"SpecValue\":\"M\"},{\"SpecId\":\"2158\",\"SpecValueId\":\"9278\",\"SpecValue\":\"栗色\"}]");
        mockSupplierProductsSubmit.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.CHECKING);
        mockSupplierProductsSubmit.setLastModify(new Date());
        mockSupplierProductsSubmit = productsRepository.saveAndFlush(mockSupplierProductsSubmit);

        mockSupplierGoodsSubmit = new HbmSupplierGoods();
        /*mockSupplierGoodsSubmit.setType(type);
        mockSupplierGoodsSubmit.setBrand(type.getBrandList().get(0));*/
        mockSupplierGoodsSubmit.setStoreId(user.getCustomerId());
        mockSupplierGoodsSubmit.setBrief("简介");
        mockSupplierGoodsSubmit.setIntro("详细介绍");
        mockSupplierGoodsSubmit.setMktPrice(mockSupplierProductsSubmit.getMktPrice());
        mockSupplierGoodsSubmit.setCost(mockSupplierProductsSubmit.getCost());
        mockSupplierGoodsSubmit.setPrice(mockSupplierProductsSubmit.getMinPrice());
        mockSupplierGoodsSubmit.setBn(mockSupplierProductsSubmit.getBn());
        mockSupplierGoodsSubmit.setName(mockSupplierProductsSubmit.getName());
        mockSupplierGoodsSubmit.setWeight(mockSupplierProductsSubmit.getWeight());
        mockSupplierGoodsSubmit.setStore(mockSupplierProductsSubmit.getStore());
        mockSupplierGoodsSubmit.setSpec("{\"2158\":\"颜色\",\"2159\":\"尺寸\"}");
        mockSupplierGoodsSubmit.setPdtDesc("[{\"ProductId\":" + mockSupplierProductsSubmit.getSupplierProductId() + ",\"Desc\":\"M,栗色\"}]");
        mockSupplierGoodsSubmit.setSpecDesc("[{\"SpecId\":2159,\"SpecValue\":\"M\",\"SpecValueId\":9286,\"SpecImage\":\"\",\"GoodsImageIds\":[]},{\"SpecId\":2158,\"SpecValue\":\"栗色\",\"SpecValueId\":9278,\"SpecImage\":\"\",\"GoodsImageIds\":[]}]");
        mockSupplierGoodsSubmit.setCreateTime(new Date());
        mockSupplierGoodsSubmit.setLastModify(new Date());
        mockSupplierGoodsSubmit.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.CHECKING);
        mockSupplierGoodsSubmit = goodsRepository.saveAndFlush(mockSupplierGoodsSubmit);
        mockSupplierProductsSubmit.setSupplierGoodsId(mockSupplierGoodsSubmit.getSupplierGoodsId());
        mockSupplierProductsSubmit = productsRepository.saveAndFlush(mockSupplierProductsSubmit);

        //已审核商品
        mockSupplierProductsChecked = new HbmSupplierProducts();
        mockSupplierProductsChecked.setBn("125");
        mockSupplierProductsChecked.setMinPrice(80);
        mockSupplierProductsChecked.setMaxPrice(80);
        mockSupplierProductsChecked.setCost(50);
        mockSupplierProductsChecked.setMktPrice(100);
        mockSupplierProductsChecked.setName("商品名称");
        mockSupplierProductsChecked.setWeight(100);
        mockSupplierProductsChecked.setStore(100);
        mockSupplierProductsChecked.setPdtDesc("M,栗色");
        mockSupplierProductsChecked.setProps("[{\"SpecId\":\"2159\",\"SpecValueId\":\"9286\",\"SpecValue\":\"M\"},{\"SpecId\":\"2158\",\"SpecValueId\":\"9278\",\"SpecValue\":\"栗色\"}]");
        mockSupplierProductsChecked.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.CHECKED);
        mockSupplierProductsChecked.setLastModify(new Date());
        mockSupplierProductsChecked = productsRepository.saveAndFlush(mockSupplierProductsChecked);

        mockSupplierGoodsChecked = new HbmSupplierGoods();
        /*mockSupplierGoodsChecked.setType(type);
        mockSupplierGoodsChecked.setBrand(type.getBrandList().get(0));*/
        mockSupplierGoodsChecked.setStoreId(user.getCustomerId());
        mockSupplierGoodsChecked.setBrief("简介");
        mockSupplierGoodsChecked.setIntro("详细介绍");
        mockSupplierGoodsChecked.setMktPrice(mockSupplierProductsSubmit.getMktPrice());
        mockSupplierGoodsChecked.setCost(mockSupplierProductsSubmit.getCost());
        mockSupplierGoodsChecked.setPrice(mockSupplierProductsSubmit.getMinPrice());
        mockSupplierGoodsChecked.setBn(mockSupplierProductsSubmit.getBn());
        mockSupplierGoodsChecked.setName(mockSupplierProductsSubmit.getName());
        mockSupplierGoodsChecked.setWeight(mockSupplierProductsSubmit.getWeight());
        mockSupplierGoodsChecked.setStore(mockSupplierProductsSubmit.getStore());
        mockSupplierGoodsChecked.setSpec("{\"2158\":\"颜色\",\"2159\":\"尺寸\"}");
        mockSupplierGoodsChecked.setPdtDesc("[{\"ProductId\":" + mockSupplierProductsSubmit.getSupplierProductId() + ",\"Desc\":\"M,栗色\"}]");
        mockSupplierGoodsChecked.setSpecDesc("[{\"SpecId\":2159,\"SpecValue\":\"M\",\"SpecValueId\":9286,\"SpecImage\":\"\",\"GoodsImageIds\":[]},{\"SpecId\":2158,\"SpecValue\":\"栗色\",\"SpecValueId\":9278,\"SpecImage\":\"\",\"GoodsImageIds\":[]}]");
        mockSupplierGoodsChecked.setCreateTime(new Date());
        mockSupplierGoodsChecked.setLastModify(new Date());
        mockSupplierGoodsChecked.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.CHECKED);
        mockSupplierGoodsChecked = goodsRepository.saveAndFlush(mockSupplierGoodsChecked);
        mockSupplierProductsChecked.setSupplierGoodsId(mockSupplierGoodsChecked.getSupplierGoodsId());
        mockSupplierProductsChecked = productsRepository.saveAndFlush(mockSupplierProductsChecked);

        //已删除商品
        mockSupplierProductsDisabled = new HbmSupplierProducts();
        mockSupplierProductsDisabled.setBn("126");
        mockSupplierProductsDisabled.setMinPrice(80);
        mockSupplierProductsDisabled.setMaxPrice(80);
        mockSupplierProductsDisabled.setCost(50);
        mockSupplierProductsDisabled.setMktPrice(100);
        mockSupplierProductsDisabled.setName("商品名称");
        mockSupplierProductsDisabled.setWeight(100);
        mockSupplierProductsDisabled.setStore(100);
        mockSupplierProductsDisabled.setPdtDesc("M,栗色");
        mockSupplierProductsDisabled.setProps("[{\"SpecId\":\"2159\",\"SpecValueId\":\"9286\",\"SpecValue\":\"M\"},{\"SpecId\":\"2158\",\"SpecValueId\":\"9278\",\"SpecValue\":\"栗色\"}]");
        mockSupplierProductsDisabled.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT);
        mockSupplierProductsDisabled.setLastModify(new Date());
        mockSupplierProductsDisabled = productsRepository.saveAndFlush(mockSupplierProductsDisabled);

        mockSupplierGoodsDisabled = new HbmSupplierGoods();
        /*mockSupplierGoodsDisabled.setType(type);
        mockSupplierGoodsDisabled.setBrand(type.getBrandList().get(0));*/
        mockSupplierGoodsDisabled.setStoreId(user.getCustomerId());
        mockSupplierGoodsDisabled.setBrief("简介");
        mockSupplierGoodsDisabled.setIntro("详细介绍");
        mockSupplierGoodsDisabled.setMktPrice(mockSupplierProductsDisabled.getMktPrice());
        mockSupplierGoodsDisabled.setCost(mockSupplierProductsDisabled.getCost());
        mockSupplierGoodsDisabled.setPrice(mockSupplierProductsDisabled.getMinPrice());
        mockSupplierGoodsDisabled.setBn(mockSupplierProductsDisabled.getBn());
        mockSupplierGoodsDisabled.setName(mockSupplierProductsDisabled.getName());
        mockSupplierGoodsDisabled.setWeight(mockSupplierProductsDisabled.getWeight());
        mockSupplierGoodsDisabled.setStore(mockSupplierProductsDisabled.getStore());
        mockSupplierGoodsDisabled.setSpec("{\"2158\":\"颜色\",\"2159\":\"尺寸\"}");
        mockSupplierGoodsDisabled.setPdtDesc("[{\"ProductId\":" + mockSupplierProductsDisabled.getSupplierProductId() + ",\"Desc\":\"M,栗色\"}]");
        mockSupplierGoodsDisabled.setSpecDesc("[{\"SpecId\":2159,\"SpecValue\":\"M\",\"SpecValueId\":9286,\"SpecImage\":\"\",\"GoodsImageIds\":[]},{\"SpecId\":2158,\"SpecValue\":\"栗色\",\"SpecValueId\":9278,\"SpecImage\":\"\",\"GoodsImageIds\":[]}]");
        mockSupplierGoodsDisabled.setCreateTime(new Date());
        mockSupplierGoodsDisabled.setLastModify(new Date());
        mockSupplierGoodsDisabled.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT);
        mockSupplierGoodsDisabled.setDisabled(true);
        mockSupplierGoodsDisabled = goodsRepository.saveAndFlush(mockSupplierGoodsDisabled);
        mockSupplierProductsDisabled.setSupplierGoodsId(mockSupplierGoodsDisabled.getSupplierGoodsId());
        mockSupplierProductsDisabled = productsRepository.saveAndFlush(mockSupplierProductsDisabled);

        //未保存商品
        mockSupplierProductsInsert = new HbmSupplierProducts();
        mockSupplierProductsInsert.setBn("127");
        mockSupplierProductsInsert.setMinPrice(8080);
        mockSupplierProductsInsert.setMaxPrice(8080);
        mockSupplierProductsInsert.setCost(5050);
        mockSupplierProductsInsert.setMktPrice(100100);
        mockSupplierProductsInsert.setName("商品名称1");
        mockSupplierProductsInsert.setWeight(100100);
        mockSupplierProductsInsert.setStore(100);
        mockSupplierProductsInsert.setPdtDesc("S,乳白色");
        mockSupplierProductsInsert.setProps("[{\"SpecId\":\"2159\",\"SpecValueId\":\"9285\",\"SpecValue\":\"S\"},{\"SpecId\":\"2158\",\"SpecValueId\":\"9265\",\"SpecValue\":\"乳白色\"}]");

        mockSupplierGoodsInsert = new HbmSupplierGoods();
        /*mockSupplierGoodsInsert.setType(type);
        mockSupplierGoodsInsert.setBrand(type.getBrandList().get(0));*/
        mockSupplierGoodsInsert.setBrief("简介123");
        mockSupplierGoodsInsert.setIntro("详细介绍123");
        mockSupplierGoodsInsert.setMktPrice(mockSupplierProductsInsert.getMktPrice());
        mockSupplierGoodsInsert.setCost(mockSupplierProductsInsert.getCost());
        mockSupplierGoodsInsert.setPrice(mockSupplierProductsInsert.getMinPrice());
        mockSupplierGoodsInsert.setBn(mockSupplierProductsInsert.getBn());
        mockSupplierGoodsInsert.setName(mockSupplierProductsInsert.getName());
        mockSupplierGoodsInsert.setWeight(mockSupplierProductsInsert.getWeight());
        mockSupplierGoodsInsert.setStore(mockSupplierProductsInsert.getStore());
        mockSupplierGoodsInsert.setSpec("{\"2158\":\"颜色\",\"2159\":\"尺寸\"}");
        mockSupplierGoodsInsert.setSpecDesc("[{\"SpecId\":2159,\"SpecValue\":\"S\",\"SpecValueId\":9285,\"SpecImage\":\"\",\"GoodsImageIds\":[]},{\"SpecId\":2158,\"SpecValue\":\"乳白色\",\"SpecValueId\":9265,\"SpecImage\":\"\",\"GoodsImageIds\":[]}]");
    }

    @Test
    public void showType() throws Exception {

    }

    /**
     * 显示商品列表
     * @throws Exception
     */
    @Test
    public void showGoodsList() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);

        HbmSupplierGoodsSearcher searcher = new HbmSupplierGoodsSearcher();
        //无搜索条件的情况
        MvcResult result1 = mockMvc.perform(post(BASE_URL + "/showGoodsList")
                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/goodsList"))
                .andReturn();
        List<HbmSupplierGoods> goodsList1 = (List<HbmSupplierGoods>) result1.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(3, goodsList1.size());

        //showCheckedOnly = true的情况
        /*MvcResult result2 = mockMvc.perform(get(BASE_URL + "/showGoodsList")
                .session(mockHttpSession)
                .param("showCheckedOnly", "true"))
                .andExpect(status().isOk())
                //返回视图名称
                .andExpect(view().name("goods/storeManager"))
                .andReturn();
        List<HbmSupplierGoods> goodsList2 = (List<HbmSupplierGoods>) result2.getModelAndView().getModel().get("goodsList");
        Assert.assertNotEquals(0, goodsList2.size());*/

        //查询未提交的
        searcher.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT.getCode());
        Page<HbmSupplierGoods> exceptGoods = goodsService.getGoodList(user.getCustomerId(), searcher);
        MvcResult result3 = mockMvc.perform(post(BASE_URL + "/showGoodsList")
                .session(mockHttpSession)
                .param("status", String.valueOf(searcher.getStatus())))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/goodsList"))
                .andExpect(model().attribute("totalRecords", exceptGoods.getTotalElements()))
                .andExpect(model().attribute("totalPages", exceptGoods.getTotalPages()))
                .andReturn();
        List<HbmSupplierGoods> realGoods = (List<HbmSupplierGoods>) result3.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(exceptGoods.getContent().size(), realGoods.size());
        Assert.assertEquals(mockSupplierGoods.getSupplierGoodsId(), realGoods.get(0).getSupplierGoodsId());
        Assert.assertTrue(realGoods.stream().allMatch(predicate -> predicate.getStatus() == StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT));

        //查询已提交
        searcher.setStatus(StoreGoodsStatusEnum.CheckStatusEnum.CHECKED.getCode());
        Page<HbmSupplierGoods> exceptGoods2 = goodsService.getGoodList(user.getCustomerId(), searcher);
        MvcResult result4 = mockMvc.perform(post(BASE_URL + "/showGoodsList")
                .session(mockHttpSession)
                .param("status", String.valueOf(searcher.getStatus())))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/goodsList"))
                .andExpect(model().attribute("totalRecords", exceptGoods2.getTotalElements()))
                .andExpect(model().attribute("totalPages", exceptGoods2.getTotalPages()))
                .andReturn();
        List<HbmSupplierGoods> realGoods2 = (List<HbmSupplierGoods>) result4.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(exceptGoods2.getContent().size(), realGoods2.size());
        Assert.assertEquals(mockSupplierGoodsChecked.getSupplierGoodsId(), realGoods2.get(0).getSupplierGoodsId());
        Assert.assertTrue(realGoods2.stream().allMatch(predicate -> predicate.getStatus() == StoreGoodsStatusEnum.CheckStatusEnum.CHECKED));

        //分页查询
        searcher.setStatus(-1);
        searcher.setPageSize(1);
        searcher.setPageNo(1);
        Page<HbmSupplierGoods> exceptGoods3 = goodsService.getGoodList(user.getCustomerId(), searcher);
        MvcResult result5 = mockMvc.perform(post(BASE_URL + "/showGoodsList")
                .param("pageSize", String.valueOf(searcher.getPageSize()))
                .param("pageNo", String.valueOf(searcher.getPageNo()))
                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/goodsList"))
                .andExpect(model().attribute("totalRecords", exceptGoods3.getTotalElements()))
                .andExpect(model().attribute("totalPages", exceptGoods3.getTotalPages()))
                .andReturn();
        List<HbmSupplierGoods> realGoods3 = (List<HbmSupplierGoods>) result5.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(exceptGoods3.getContent().size(), realGoods3.size());
        Assert.assertEquals(mockSupplierGoodsChecked.getSupplierGoodsId(), realGoods3.get(0).getSupplierGoodsId());
    }

    /**
     * 更新商品状态
     * @throws Exception
     */
    @Test
    public void updateGoodStatus() throws Exception {
        MockHttpSession session = loginAs(userName, passWord);

        //当goodsId或者status为空时，应返回信息没有传输数据
        MvcResult result1 = mockMvc.perform(post(BASE_URL + "/updateGoodStatus")
                .session(session)
                ).andExpect(status().isOk())
                .andReturn();
        String content1 = new String(result1.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj1 = JSONObject.parseObject(content1);
        Assert.assertEquals("没有传输数据", obj1.get("msg"));

        //当goodsId不存在时
        MvcResult result2 = mockMvc.perform(post(BASE_URL + "/updateGoodStatus")
                .session(session)
                .param("goodsId", String.valueOf(mockSupplierGoods.getSupplierGoodsId() + 100))
                .param("status", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String content2 = new String(result2.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj2 = JSONObject.parseObject(content2);
        Assert.assertEquals("商品编号错误！", obj2.get("msg"));

        //当商品已审核
        MvcResult result3 = mockMvc.perform(post(BASE_URL + "/updateGoodStatus")
                .session(session)
                .param("goodsId", String.valueOf(mockSupplierGoodsChecked.getSupplierGoodsId()))
                .param("status", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String content3 = new String(result3.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj3 = JSONObject.parseObject(content3);
        Assert.assertEquals("该商品已审核或已回收，无法操作！", obj3.get("msg"));

        //正确的更新商品
        MvcResult result4 = mockMvc.perform(post(BASE_URL + "/updateGoodStatus")
                .session(session)
                .param("goodsId", String.valueOf(mockSupplierGoods.getSupplierGoodsId()))
                .param("status", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String content4 = new String(result4.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj4 = JSONObject.parseObject(content4);
        Assert.assertEquals(200, obj4.get("code"));

        MvcResult result5 = mockMvc.perform(post(BASE_URL + "/updateGoodStatus")
                .session(session)
                .param("goodsId", String.valueOf(mockSupplierGoodsChecked.getSupplierGoodsId()))
                .param("status", "4"))
                .andExpect(status().isOk())
                .andReturn();
        String content5 = new String(result5.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj5 = JSONObject.parseObject(content5);
        Assert.assertEquals(200, obj5.get("code"));
    }

    /**
     * 删除商品
     * @throws Exception
     */
    @Test
    public void deleteGood() throws Exception {
        MockHttpSession session = loginAs(userName, passWord);

        //goodsId 为空
        MvcResult result1 = mockMvc.perform(post(BASE_URL + "/deleteGood")
                .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String content1 = new String(result1.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj1 = JSONObject.parseObject(content1);
        Assert.assertEquals("没有传输数据", obj1.get("msg"));

        //goodsId 不存在
        MvcResult result2 = mockMvc.perform(post(BASE_URL + "/deleteGood")
                .session(session)
                .param("goodsId", String.valueOf(mockSupplierGoods.getSupplierGoodsId() + 100))
                .param("status", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String content2 = new String(result2.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj2 = JSONObject.parseObject(content2);
        Assert.assertEquals("商品编号错误！", obj2.get("msg"));

        //3.存储的数据的正确性
        MvcResult result4 = mockMvc.perform(post(BASE_URL + "/deleteGood")
                .session(session)
                .param("goodsId", String.valueOf(mockSupplierGoods.getSupplierGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String content4 = new String(result4.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj4 = JSONObject.parseObject(content4);
        Assert.assertEquals("请求成功", obj4.get("msg"));
        HbmSupplierGoods exceptGoods = goodsRepository.findOne(mockSupplierGoods.getSupplierGoodsId());
        Assert.assertEquals(exceptGoods.isDisabled(), true);
    }

}