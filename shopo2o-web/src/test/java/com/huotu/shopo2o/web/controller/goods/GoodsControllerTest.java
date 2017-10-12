package com.huotu.shopo2o.web.controller.goods;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.good.HbmBrand;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmGoodsTypeSpec;
import com.huotu.shopo2o.service.entity.good.HbmSpecValues;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import com.huotu.shopo2o.service.model.HbmSupplierGoodsSearcher;
import com.huotu.shopo2o.service.repository.good.HbmSupplierGoodsRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierProductsRepository;
import com.huotu.shopo2o.service.service.goods.HbmGoodsTypeService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierGoodsService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierProductsService;
import com.huotu.shopo2o.web.CommonTestBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    /**
     * 显示类型
     *
     * @throws Exception
     */
    @Test
    public void showType() throws Exception {
        //添加类目
        int a = 10;
        while (a-- > 0) {
            mockHbmGoodsType(false, "0");
        }
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        MvcResult result1 = mockMvc.perform(post(BASE_URL + "/showType")
                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/goodType"))
                .andReturn();
        List<HbmGoodsType> typeList = (List<HbmGoodsType>) result1.getModelAndView().getModel().get("typeList");
        Assert.assertTrue(typeList.size() == 10);

    }

    /**
     * 根据标准类目ID，获取其子类目LIST
     *
     * @throws Exception
     */
    @Test
    public void showGoodsType() throws Exception {

        //添加父类目
        HbmGoodsType hbmGoodsType = mockHbmGoodsType(true, "0");
        //添加类目
        int a = 10;
        while (a-- > 0) {
            mockHbmGoodsType(false, hbmGoodsType.getStandardTypeId());
        }

        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        Map<String, Object> result = JsonPath.read(mockMvc.perform(post(BASE_URL + "/getType")
                .param("standardTypeId", hbmGoodsType.getStandardTypeId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$");

        List<ApiResult> apiResults = (List<ApiResult>) result.get("data");
        Assert.assertTrue(apiResults.size() == 10);

    }

    /**
     * 添加商品时，根据选择的类目ID获取相关信息
     * 1.参数 standardTypeId 为空
     * 2.参数 standardTypeId 不存在
     * 3.参数 standardTypeId 200 存在子类目
     * 4.参数 standardTypeId 50011167 羽绒服
     *
     * @throws Exception
     */
    @Test
    public void addGood() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        //1.参数 standardTypeId 为空
        MvcResult result1 = mockMvc.perform(get("/good/addGood")
                .session(mockHttpSession))
                //302 跳转
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/good/showType"))
                .andReturn();

        //2.参数 standardTypeId 不存在
        MvcResult result2 = mockMvc.perform(get("/good/addGood")
                .session(mockHttpSession)
                .param("standardTypeId", "-1"))
                //302 跳转
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/good/showType"))
                .andReturn();

        //一级父类目,存在子类目
        HbmGoodsType hbmGoodsType = mockHbmGoodsType(true, "0");
        mockHbmGoodsType(false, hbmGoodsType.getStandardTypeId());
        MvcResult result3 = mockMvc.perform(get("/good/addGood")
                .session(mockHttpSession)
                .param("standardTypeId", hbmGoodsType.getStandardTypeId()))
                //302 跳转
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/good/showType"))
                .andReturn();

        //4.参数 standardTypeId 50011167 羽绒服,规格共2个，第一个为尺寸(1627207)，第二个为规格(20509)
        //添加类型和品牌
        HbmGoodsType hbmGoodsType1 = mockHbmGoodsType(false, "0");
        //添加品牌
        HbmBrand hbmBrand = mockHbmBrand();
        //品牌和类型关联
        mockHbmTypeBrand(hbmGoodsType1.getTypeId(), hbmBrand.getBrandId());
        //添加两个规格
        HbmSpecification hbmSpecification = mockHbmSpecification();
        HbmSpecification hbmSpecification1 = mockHbmSpecification();
        //添加规格值
        HbmSpecValues hbmSpecValues = mockHbmSpecValues(hbmSpecification.getSpecId());
        HbmSpecValues hbmSpecValues1 = mockHbmSpecValues(hbmSpecification1.getSpecId());

        //类型和规格关联
        HbmGoodsTypeSpec hbmGoodsTypeSpec = mockHbmGoodsTypeSpec(hbmGoodsType1.getTypeId(),hbmSpecification.getSpecId(),hbmSpecValues.getId());
        HbmGoodsTypeSpec hbmGoodsTypeSpec1 = mockHbmGoodsTypeSpec(hbmGoodsType1.getTypeId(),hbmSpecification1.getSpecId(),hbmSpecValues1.getId());

        //商户配置
        mockMallCustomerConfig(1);

        MvcResult result4 = mockMvc.perform(get("/good/addGood")
                .session(mockHttpSession)
                .param("standardTypeId", hbmGoodsType1.getStandardTypeId()))
                .andExpect(status().isOk())
                .andExpect(view().name("goods/addGood"))
                .andExpect(model().attributeExists("typeId"))
                .andExpect(model().attributeExists("typePath"))
                .andExpect(model().attributeExists("productBn"))
                .andExpect(model().attributeExists("specList"))
                .andExpect(model().attributeExists("brandList"))
                .andReturn();
        Integer typeId = (Integer) result4.getModelAndView().getModel().get("typeId");
        String typePath = (String) result4.getModelAndView().getModel().get("typePath");
        List<HbmSpecification> specList = (List<HbmSpecification>) result4.getModelAndView().getModel().get("specList");
        List<HbmBrand> brandList = (List<HbmBrand>) result4.getModelAndView().getModel().get("brandList");

        //期望返回数据
        HbmGoodsType expectType = typeService.getGoodsTypeByStandardTypeId(hbmGoodsType1.getStandardTypeId());
        List<HbmSpecification> expectSpecList = expectType.getSpecList();
        List<HbmBrand> expectBrandList = expectType.getBrandList();

        Assert.assertEquals(expectType.getTypeId(), typeId);
        Assert.assertEquals(expectSpecList.size(), specList.size());
        Assert.assertEquals(expectSpecList.size(), specList.size());
        Assert.assertEquals(hbmSpecification.getSpecId(), specList.get(0).getSpecId());
        Assert.assertEquals(hbmSpecification1.getSpecId(), specList.get(1).getSpecId());

        Assert.assertEquals(expectBrandList.size(), brandList.size());
    }



    /**
     * 保存或更新商品测试
     *
     * @throws Exception
     */
    @Test
    public void updateGood() throws Exception {
        //添加类型
        HbmGoodsType hbmGoodsType = mockHbmGoodsType(false, "0");
        //添加品牌
        HbmBrand hbmBrand = mockHbmBrand();
        //品牌和类型关联
        mockHbmTypeBrand(hbmGoodsType.getTypeId(), hbmBrand.getBrandId());

        MockHttpSession mockHttpSession = loginAs(userName, passWord);

        Map<String, Object> result = JsonPath.read(mockMvc.perform(post(BASE_URL + "/updateGood")
                .param("type.typeId", hbmGoodsType.getTypeId().toString())
                .param("name", "商品名称" + UUID.randomUUID().toString())
                .param("goodStatus", "1")
                .param("specDesc", "[{\"SpecId\":\"7904\",\"SpecValue\":\"红色\",\"SpecValueId\":61500,\"SpecImage\":\"\",\"GoodsImageIds\":[]},{\"SpecId\":\"7905\",\"SpecValue\":\"官方标配\",\"SpecValueId\":61515,\"SpecImage\":\"\",\"GoodsImageIds\":[]},{\"SpecId\":\"7932\",\"SpecValue\":\"64G以上\",\"SpecValueId\":65322,\"SpecImage\":\"\",\"GoodsImageIds\":[]},{\"SpecId\":\"7929\",\"SpecValue\":\"中国大陆\",\"SpecValueId\":65269,\"SpecImage\":\"\",\"GoodsImageIds\":[]}]")
                .param("brand.brandId", hbmBrand.getBrandId().toString())
                .param("subTitle", "商品描述" + UUID.randomUUID().toString())
                .param("specList", "[{\"supplierProductId\":\"0\",\"minPrice\":\"133\",\"maxPrice\":\"133\",\"cost\":\"122\",\"bn\":\"56950dxHAQstX-1-1\",\"weight\":\"23\",\"store\":\"122\",\"pdtDesc\":\"红色,官方标配,64G以上,中国大陆\",\"props\":\"[{\\\"SpecId\\\":\\\"7904\\\",\\\"SpecValueId\\\":\\\"61500\\\",\\\"SpecValue\\\":\\\"红色\\\"},{\\\"SpecId\\\":\\\"7905\\\",\\\"SpecValueId\\\":\\\"61515\\\",\\\"SpecValue\\\":\\\"官方标配\\\"},{\\\"SpecId\\\":\\\"7932\\\",\\\"SpecValueId\\\":\\\"65322\\\",\\\"SpecValue\\\":\\\"64G以上\\\"},{\\\"SpecId\\\":\\\"7929\\\",\\\"SpecValueId\\\":\\\"65269\\\",\\\"SpecValue\\\":\\\"中国大陆\\\"}]\",\"userPriceInfo\":\"648:34:244|785:34:244|786:34:244|789:34:244|647:34:244\",\"userIntegralInfo\":\"\",\"minUserPrice\":34}]")
                .param("hidGoodsImagePath", "{'bigPic':'/resource/images/photo/56950/2017050910472245181627.png','smallPic':'/resource/images/photo/56950/2017050910472245181627.png','thumbPic':'/resource/images/photo/56950/2017050910472245181627.png'}")
                .contentType(MediaType.APPLICATION_JSON)
                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$");

        //1.新增商品 类型不存在
        List<HbmSupplierProducts> insertProducts = new ArrayList<>();
        insertProducts.add(mockSupplierProductsInsert);
        String specList1 = JSONArray.toJSONString(insertProducts);

        MvcResult result1 = mockMvc.perform(get("/good/updateGood")
                .session(mockHttpSession)
                .param("hidGoodsImagePath", "{'bigPic':'/resource/images/photo/56950/2017050910472245181627.png','smallPic':'/resource/images/photo/56950/2017050910472245181627.png','thumbPic':'/resource/images/photo/56950/2017050910472245181627.png'}")
                .param("name", mockSupplierGoodsInsert.getName())
                .param("spec", mockSupplierGoodsInsert.getSpec())
                .param("specDesc", mockSupplierGoodsInsert.getSpecDesc())
                .param("goodStatus", "0")
                .param("specList", specList1))
                .andExpect(status().isOk())
                .andReturn();
        String content1 = new String(result1.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj1 = JSONObject.parseObject(content1);
        Assert.assertEquals("请先选择商品类型！", obj1.get("msg"));

        //2.新增商品 货品未录入
        MvcResult result2 = mockMvc.perform(get("/good/updateGood")
                .session(mockHttpSession)
                .param("name", mockSupplierGoodsInsert.getName())
                .param("goodStatus", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String content2 = new String(result2.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj2 = JSONObject.parseObject(content2);
        Assert.assertEquals("请选择规格！", obj2.get("msg"));

        //3.新增商品
        insertProducts.clear();
        insertProducts.add(mockSupplierProductsInsert);
        String specList2 = JSONArray.toJSONString(insertProducts);

        mockSupplierGoodsInsert.setType(mockHbmGoodsType(false, "0"));

        MvcResult result3 = mockMvc.perform(get("/good/updateGood")
                .session(mockHttpSession)
                .param("hidGoodsImagePath", "{'bigPic':'/resource/images/photo/56950/2017050910472245181627.png','smallPic':'/resource/images/photo/56950/2017050910472245181627.png','thumbPic':'/resource/images/photo/56950/2017050910472245181627.png'}")
                .param("name", mockSupplierGoodsInsert.getName())
                .param("type.typeId", mockSupplierGoodsInsert.getType().getTypeId().toString())
                .param("spec", mockSupplierGoodsInsert.getSpec())
                .param("specDesc", mockSupplierGoodsInsert.getSpecDesc())
                .param("goodStatus", "0")
                .param("specList", specList2))
                .andExpect(status().isOk())
                .andReturn();
        String content3 = new String(result3.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj3 = JSONObject.parseObject(content3);
        Assert.assertEquals(200, obj3.get("code"));

        //5.编辑商品 商品已删除
        insertProducts.clear();
        insertProducts.add(mockSupplierProductsDisabled);
        String specList5 = JSONArray.toJSONString(insertProducts);

        mockSupplierGoodsDisabled.setType(mockHbmGoodsType(false, "0"));
        MvcResult result5 = mockMvc.perform(get("/good/updateGood")
                .session(mockHttpSession)
                .param("hidGoodsImagePath", "{'bigPic':'/resource/images/photo/56950/2017050910472245181627.png','smallPic':'/resource/images/photo/56950/2017050910472245181627.png','thumbPic':'/resource/images/photo/56950/2017050910472245181627.png'}")
                .param("supplierGoodsId", mockSupplierGoodsDisabled.getSupplierGoodsId().toString())
                .param("name", mockSupplierGoodsDisabled.getName())
                .param("type.typeId", mockSupplierGoodsDisabled.getType().getTypeId().toString())
                .param("spec", mockSupplierGoodsDisabled.getSpec())
                .param("specDesc", mockSupplierGoodsDisabled.getSpecDesc())
                .param("goodStatus", "0")
                .param("specList", specList5))
                .andExpect(status().isOk())
                .andReturn();
        String content5 = new String(result5.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj5 = JSONObject.parseObject(content5);
        Assert.assertEquals("商品已删除，无法编辑！", obj5.get("msg"));
        //6.编辑商品 类型不存在
        insertProducts.clear();
        insertProducts.add(mockSupplierProducts);
        String specList6 = JSONArray.toJSONString(insertProducts);

        MvcResult result6 = mockMvc.perform(get("/good/updateGood")
                .session(mockHttpSession)
                .param("hidGoodsImagePath", "{'bigPic':'/resource/images/photo/56950/2017050910472245181627.png','smallPic':'/resource/images/photo/56950/2017050910472245181627.png','thumbPic':'/resource/images/photo/56950/2017050910472245181627.png'}")
                .param("supplierGoodsId", mockSupplierGoods.getSupplierGoodsId().toString())
                .param("name", mockSupplierGoods.getName())
                .param("spec", mockSupplierGoods.getSpec())
                .param("specDesc", mockSupplierGoods.getSpecDesc())
                .param("goodStatus", "0")
                .param("specList", specList6))
                .andExpect(status().isOk())
                .andReturn();
        String content6 = new String(result6.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj6 = JSONObject.parseObject(content6);
        Assert.assertEquals("请先选择商品类型！", obj6.get("msg"));

        //7.编辑商品 货品未录入
        MvcResult result7 = mockMvc.perform(get("/good/updateGood")
                .session(mockHttpSession)
                .param("hidGoodsImagePath", "{'bigPic':'/resource/images/photo/56950/2017050910472245181627.png','smallPic':'/resource/images/photo/56950/2017050910472245181627.png','thumbPic':'/resource/images/photo/56950/2017050910472245181627.png'}")
                .param("supplierGoodsId", mockSupplierGoods.getSupplierGoodsId().toString())
                .param("name", mockSupplierGoods.getName())
                .param("type.typeId", mockSupplierGoodsDisabled.getType().getTypeId().toString())
                .param("spec", mockSupplierGoods.getSpec())
                .param("specDesc", mockSupplierGoods.getSpecDesc())
                .param("goodStatus", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String content7 = new String(result7.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj7 = JSONObject.parseObject(content7);
        Assert.assertEquals("货品未录入！", obj7.get("msg"));

        //8.编辑货品,修改货品 M,栗色 为 S,乳白色
        insertProducts.clear();
        insertProducts.add(mockSupplierProductsInsert);
        String specList8 = JSONArray.toJSONString(insertProducts);

        mockSupplierGoods.setType(mockHbmGoodsType(false, "0"));
        MvcResult result8 = mockMvc.perform(get("/good/updateGood")
                .session(mockHttpSession)
                .param("supplierGoodsId", mockSupplierGoods.getSupplierGoodsId().toString())
                .param("type.typeId", mockSupplierGoods.getType().getTypeId().toString())
                .param("name", mockSupplierGoodsInsert.getName())
                .param("spec", mockSupplierGoodsInsert.getSpec())
                .param("specDesc", mockSupplierGoodsInsert.getSpecDesc())
                .param("hidGoodsImagePath", "{'bigPic':'/resource/images/photo/56950/2017050910472245181627.png','smallPic':'/resource/images/photo/56950/2017050910472245181627.png','thumbPic':'/resource/images/photo/56950/2017050910472245181627.png'}")
                .param("goodStatus", "0")
                .param("specList", specList8))
                .andExpect(status().isOk())
                .andReturn();
        String content8 = new String(result8.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj8 = JSONObject.parseObject(content8);
        Assert.assertEquals(200, obj8.get("code"));

        HbmSupplierGoods realGood8 = goodsService.findById(mockSupplierGoods.getSupplierGoodsId());
        Assert.assertEquals(mockSupplierGoodsInsert.getName(), realGood8.getName());
        Assert.assertEquals(mockSupplierGoodsInsert.getSpecDesc(), realGood8.getSpecDesc());

        List<HbmSupplierProducts> realProduct8 = productsService.getProductListByGoodId(mockSupplierGoods.getSupplierGoodsId());
        Assert.assertEquals(1, realProduct8.size());
        Assert.assertEquals(mockSupplierProductsInsert.getProps(), realProduct8.get(0).getProps());
    }

    /**
     * 显示商品列表
     *
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
     *
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
     *
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