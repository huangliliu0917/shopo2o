package com.huotu.shopo2o.web;

import com.huotu.shopo2o.common.ienum.RebateCompatibleEnum;
import com.huotu.shopo2o.service.config.MallPasswordEncoder;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.author.Operator;
import com.huotu.shopo2o.service.entity.config.MallCustomerConfig;
import com.huotu.shopo2o.service.entity.config.SupBasicConfig;
import com.huotu.shopo2o.service.entity.config.SupShopConfig;
import com.huotu.shopo2o.service.entity.good.HbmBrand;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmGoodsTypeSpec;
import com.huotu.shopo2o.service.entity.good.HbmSpecValues;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import com.huotu.shopo2o.service.entity.good.HbmTypeBrand;
import com.huotu.shopo2o.service.entity.good.MallGood;
import com.huotu.shopo2o.service.entity.good.MallProduct;
import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.enums.Authority;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.enums.OrderEnum;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.service.repository.author.OperatorRepository;
import com.huotu.shopo2o.service.repository.config.MallCustomerConfigRepository;
import com.huotu.shopo2o.service.repository.config.SupBasicConfigRepository;
import com.huotu.shopo2o.service.repository.good.HbmBrandRepository;
import com.huotu.shopo2o.service.repository.good.HbmGoodsTypeRepository;
import com.huotu.shopo2o.service.repository.good.HbmGoodsTypeSpecRepository;
import com.huotu.shopo2o.service.repository.good.HbmSpecValuesRepository;
import com.huotu.shopo2o.service.repository.good.HbmSpecificationRepository;
import com.huotu.shopo2o.service.repository.good.HbmTypeBrandRepository;
import com.huotu.shopo2o.service.repository.good.MallGoodRepository;
import com.huotu.shopo2o.service.repository.good.MallProductRepository;
import com.huotu.shopo2o.service.repository.order.MallAfterSalesRepository;
import com.huotu.shopo2o.service.repository.order.MallDeliveryRepository;
import com.huotu.shopo2o.service.repository.order.MallOrderRepository;
import com.huotu.shopo2o.service.repository.store.StoreRepository;
import com.huotu.shopo2o.service.repository.store.SupShopCatRepository;
import com.huotu.shopo2o.web.config.MVCConfig;
import com.huotu.shopo2o.web.config.SecurityConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by helloztt on 2017-08-23.
 */
@WebAppConfiguration
@ActiveProfiles({"test", "unit_test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MVCConfig.class, SecurityConfig.class})
public class CommonTestBase extends SpringWebTest {
    @Autowired
    protected MallCustomerRepository mallCustomerRepository;
    @Autowired
    protected MallAfterSalesRepository mallAfterSalesRepository;
    @Autowired
    protected StoreRepository storeRepository;
    @Autowired
    protected MallOrderRepository mallOrderRepository;
    @Autowired
    protected MallDeliveryRepository mallDeliveryRepository;
    @Autowired
    protected OperatorRepository operatorRepository;
    @Autowired
    protected SupShopCatRepository supShopCatRepository;
    @Autowired
    protected SupBasicConfigRepository supBasicConfigRepository;
    @Autowired
    protected MallPasswordEncoder passwordEncoder;
    @Autowired
    protected HbmGoodsTypeRepository hbmGoodsTypeRepository;
    @Autowired
    protected HbmBrandRepository hbmBrandRepository;
    @Autowired
    protected MallGoodRepository mallGoodRepository;
    @Autowired
    protected MallProductRepository mallProductRepository;

    @Autowired
    protected HbmTypeBrandRepository hbmTypeBrandRepository;

    @Autowired
    private HbmSpecificationRepository hbmSpecificationRepository;

    @Autowired
    private HbmSpecValuesRepository hbmSpecValuesRepository;

    @Autowired
    private HbmGoodsTypeSpecRepository hbmGoodsTypeSpecRepository;
    @Autowired
    private MallCustomerConfigRepository mallCustomerConfigRepository;

    protected Random random = new Random();

    protected MockHttpSession loginAs(String userName, String password) throws Exception {
        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/code/verifyImage"))
                .andReturn().getRequest().getSession(true);
        String verifyCode = session.getAttribute("verifyCode").toString();
        session = (MockHttpSession) this.mockMvc.perform(MockMvcRequestBuilders.post("/login").session(session)
                .param("username", userName)
                .param("password", password)
                .param("roleType", String.valueOf(CustomerTypeEnum.HUOBAN_MALL.getCode()))
                .param("verifyCode", verifyCode)
        )
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        saveAuthedSession(session);
        return session;
    }
    protected MallOrder mockMallOrder(int storeId) {
        MallOrder mallOrder = new MallOrder();
        mallOrder.setOrderId(UUID.randomUUID().toString());
        mallOrder.setStoreId(storeId);
        mallOrder.setOrderStatus(OrderEnum.OrderStatus.ACTIVE);
        mallOrder.setOrderSourceType(OrderEnum.OrderSourceType.NORMAL);
        mallOrder.setPayStatus(OrderEnum.PayStatus.PAYED);
        mallOrder.setPaymentType(OrderEnum.PaymentOptions.ALIPEY_WEP);
        mallOrder.setCreateTime(new Date());
        return mallOrderRepository.saveAndFlush(mallOrder);
    }

    protected MallDelivery mockMallDeliveryOrder(Store store, MallOrder mallOrder, String type) {
        MallDelivery mallDelivery = new MallDelivery();
        mallDelivery.setDeliveryId(UUID.randomUUID().toString());
        mallDelivery.setStore(store);
        mallDelivery.setOrder(mallOrder);
        mallDelivery.setType(type);
        mallDelivery.setCreateTime(new Date());
        return mallDeliveryRepository.saveAndFlush(mallDelivery);
    }

    protected MallAfterSales mockMallAfterSales(MallCustomer mallCustomer, MallOrder mallOrder) {
        MallAfterSales mallAfterSales = new MallAfterSales();
        mallAfterSales.setStoreId(mallCustomer.getCustomerId());
        mallAfterSales.setOrderId(mallOrder.getOrderId());
        mallAfterSales.setAfterId(UUID.randomUUID().toString());
        mallAfterSales.setCreateTime(new Date());
        mallAfterSales.setAfterSaleType(AfterSaleEnum.AfterSaleType.REFUND);
        mallAfterSales.setAfterSaleStatus(AfterSaleEnum.AfterSaleStatus.REFUNDING);
        mallAfterSales.setAfterSalesReason(AfterSaleEnum.AfterSalesReason.ONTER_REASON);
        mallAfterSales.setPayStatus(OrderEnum.PayStatus.PAYED);
        return mallAfterSalesRepository.saveAndFlush(mallAfterSales);
    }

    protected Operator mockOperator(MallCustomer mallCustomer) {
        Operator operator = new Operator();
        operator.setCustomer(mallCustomer);
        operator.setCustomerType(mallCustomer.getCustomerType());
        operator.setDeleted(false);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(Authority.STORE_ORDER);
        authorities.add(Authority.SUPPLIER_SHOP);
        operator.setAuthoritySet(authorities);
        return operatorRepository.saveAndFlush(operator);
    }

    protected SupShopCat mockSupShopCat(MallCustomer mallCustomer){
        SupShopCat shopCat = new SupShopCat();
        shopCat.setParentId(0);
        shopCat.setStoreId(mallCustomer.getStore().getId());
        return supShopCatRepository.saveAndFlush(shopCat);
    }
    protected SupShopCat mockSupShopCatWithoutSave(MallCustomer mallCustomer){
        SupShopCat shopCat = new SupShopCat();
        shopCat.setParentId(0);
        shopCat.setStoreId(mallCustomer.getStore().getId());
        return shopCat;
    }
    protected SupBasicConfig mockSupBasicConfig(Store store,SupShopConfig supShopConfig){
        SupBasicConfig supBasicConfig = new SupBasicConfig();
        supBasicConfig.setStoreId(store.getId());
        supBasicConfig.setShopConfig(supShopConfig);
        return supBasicConfigRepository.saveAndFlush(supBasicConfig);
    }
    protected SupShopConfig mockSupShopConfig(){
        SupShopConfig supShopConfig = new SupShopConfig();
        supShopConfig.setShopName(UUID.randomUUID().toString());
        supShopConfig.setDisabled(false);
        supShopConfig.setShopLogo(UUID.randomUUID().toString());
        return supShopConfig;
    }

    /**
     * 添加类型
     * @param isParent
     * @param parentStandardTypeId
     * @return
     */
    protected HbmGoodsType mockHbmGoodsType(Boolean isParent,String parentStandardTypeId){
        HbmGoodsType hbmGoodsType = new HbmGoodsType();
        hbmGoodsType.setStandardTypeId(RandomStringUtils.randomNumeric(5));
        hbmGoodsType.setName("类型名称"+UUID.randomUUID().toString());
        hbmGoodsType.setParent(isParent);
        hbmGoodsType.setDisabled(false);
        hbmGoodsType.setCustomerId(-1);
        hbmGoodsType.setParentStandardTypeId(parentStandardTypeId);
        return hbmGoodsTypeRepository.saveAndFlush(hbmGoodsType);
    }

    protected HbmBrand mockHbmBrand(){
        HbmBrand hbmBrand = new HbmBrand();
        hbmBrand.setBrandName("品牌名称"+UUID.randomUUID().toString());
        hbmBrand.setCustomerId(-1);
        return hbmBrandRepository.saveAndFlush(hbmBrand);

    }

    protected HbmTypeBrand mockHbmTypeBrand(int typeId,int brandId){
        HbmTypeBrand hbmTypeBrand = new HbmTypeBrand();
        hbmTypeBrand.setTypeId(typeId);
        hbmTypeBrand.setBrandId(brandId);
        return hbmTypeBrandRepository.saveAndFlush(hbmTypeBrand);
    }

    private int i;
    protected HbmSpecification mockHbmSpecification(){

        HbmSpecification hbmSpecification = new HbmSpecification();
        hbmSpecification.setOrder(++i);
        hbmSpecification.setSpecName("规格名"+UUID.randomUUID().toString());
        return hbmSpecificationRepository.saveAndFlush(hbmSpecification);
    }

    protected HbmSpecValues mockHbmSpecValues(Integer specId) {
        HbmSpecValues hbmSpecValues = new HbmSpecValues();
        hbmSpecValues.setSpecId(specId);
        return hbmSpecValuesRepository.saveAndFlush(hbmSpecValues);
    }

    protected HbmGoodsTypeSpec mockHbmGoodsTypeSpec(Integer typeId, Integer specId, Integer specValuesId) {
        HbmGoodsTypeSpec hbmGoodsTypeSpec = new HbmGoodsTypeSpec();
        hbmGoodsTypeSpec.setTypeId(typeId);
        hbmGoodsTypeSpec.setSpecId(specId);
        hbmGoodsTypeSpec.setSpecValueId(specValuesId);
        hbmGoodsTypeSpec.setCustomerId(-1);
        return hbmGoodsTypeSpecRepository.saveAndFlush(hbmGoodsTypeSpec);
    }

    protected MallCustomerConfig mockMallCustomerConfig(Integer customerId){
        MallCustomerConfig mallCustomerConfig = new MallCustomerConfig();
        mallCustomerConfig.setCustomerId(customerId);
        mallCustomerConfig.setDisRebateCalculateMode(0);
        mallCustomerConfig.setRebateCompatibleEnum(RebateCompatibleEnum.eightMode);
        return mallCustomerConfigRepository.saveAndFlush(mallCustomerConfig);
    }

    protected MallGood mockMallGood(HbmSupplierGoods supplierGoods, List<MallProduct> productss) {
        MallGood mallGood = new MallGood();
        mallGood.setTypeId(supplierGoods.getType() != null? supplierGoods.getType().getTypeId(): null);
        mallGood.setStoreCatId(supplierGoods.getShopCat() != null? supplierGoods.getShopCat().getCatId(): null);
        mallGood.setBigPic(supplierGoods.getBigPic());
        mallGood.setBn(supplierGoods.getBn());
        mallGood.setBrief(supplierGoods.getBrief());
        mallGood.setCost(supplierGoods.getCost());
        mallGood.setCustomerId(new Long(supplierGoods.getStoreId()).intValue());
        mallGood.setDisabled(supplierGoods.isDisabled());
        mallGood.setIntro(supplierGoods.getIntro());
        mallGood.setLimitBuyNum(supplierGoods.getLimitBuyNum());
        mallGood.setMarketable(supplierGoods.isMarketable());
        mallGood.setMktPrice(supplierGoods.getMktPrice());
        mallGood.setName(supplierGoods.getName());
        mallGood.setNotifyNum(supplierGoods.getNotifyNum());
        mallGood.setPrice(supplierGoods.getPrice());
        mallGood.setProducts(productss);
        mallGood.setSmallPic(supplierGoods.getSmallPic());
        mallGood.setSpec(supplierGoods.getSpec());
        mallGood.setSpecDesc(supplierGoods.getSpecDesc());
        mallGood.setStoreId(supplierGoods.getStoreId());
        mallGood.setSubTitle(supplierGoods.getSubTitle());
        mallGood.setThumbnailPic(supplierGoods.getThumbnailPic());
        mallGood.setUnit(supplierGoods.getUnit());
        mallGood.setWeight(supplierGoods.getWeight());
        return mallGoodRepository.saveAndFlush(mallGood);
    }

    protected MallProduct mockMallProduct(HbmSupplierProducts supplierProducts) {
        MallProduct mallProduct = new MallProduct();
        mallProduct.setCost(supplierProducts.getCost());
        mallProduct.setWeight(supplierProducts.getWeight());
        mallProduct.setUnit(supplierProducts.getUnit());
        mallProduct.setBarcode(supplierProducts.getBarcode());
        mallProduct.setBn(supplierProducts.getBn());
        mallProduct.setFreez(supplierProducts.getFreez());
        mallProduct.setDisRebateCustomPercent(supplierProducts.getDisRebateCustomPercent());
        mallProduct.setLastModify(new Date());
        mallProduct.setMarketable(supplierProducts.getMarketable());
        mallProduct.setMktPrice(supplierProducts.getMktPrice());
        mallProduct.setName(supplierProducts.getName());
        mallProduct.setPrice(150);
        mallProduct.setProps(supplierProducts.getProps());
        mallProduct.setStandard(supplierProducts.getPdtDesc());
        mallProduct.setStore(supplierProducts.getStore());
        mallProduct.setMarketable(1);
        mallProduct.setSupplierProductId(supplierProducts.getSupplierProductId());
        mallProduct.setTitle(supplierProducts.getTitle());
        mallProduct.setUserIntegralInfo(supplierProducts.getUserIntegralInfo());
        mallProduct.setUserPriceInfo(supplierProducts.getUserPriceInfo());

        return mallProductRepository.saveAndFlush(mallProduct);
    }
}
