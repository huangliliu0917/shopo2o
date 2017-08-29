package com.huotu.shopo2o.hbm.web;

import com.huotu.shopo2o.hbm.web.config.MVCConfig;
import com.huotu.shopo2o.service.config.MallPasswordEncoder;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.Shop;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.service.repository.ShopRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;

/**
 * Created by helloztt on 2017-08-23.
 */
@WebAppConfiguration
@ActiveProfiles({"test", "unit_test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MVCConfig.class})
public class CommonTestBase extends SpringWebTest {
    @Autowired
    protected MallCustomerRepository mallCustomerRepository;
    @Autowired
    protected ShopRepository shopRepository;
    @Autowired
    private MallPasswordEncoder passwordEncoder;

    protected Random random = new Random();
    protected String customerCookiesName = "UserID";

    protected MallCustomer mockCustomer(){
        MallCustomer customer = new MallCustomer();
        customer.setUsername(UUID.randomUUID().toString());
        customer.setNickName(UUID.randomUUID().toString());
        customer.setPassword(UUID.randomUUID().toString());
        customer.setCustomerType(CustomerTypeEnum.HUOBAN_MALL);
        return mallCustomerRepository.saveAndFlush(customer);
    }

    protected Shop mockShop(MallCustomer shopCustomer,MallCustomer customer){
        Shop shop = new Shop();
        shop.setId(shopCustomer.getCustomerId());
        shop.setCustomer(customer);
        shop.setName(UUID.randomUUID().toString());
        shop.setAreaCode(UUID.randomUUID().toString());
        shop.setTelephone(UUID.randomUUID().toString());
        shop.setProvinceCode(UUID.randomUUID().toString());
        shop.setCityCode(UUID.randomUUID().toString());
        shop.setDistrictCode(UUID.randomUUID().toString());
        shop.setAddress(UUID.randomUUID().toString());
        shop.setLogo(UUID.randomUUID().toString());
        shop.setErpId(UUID.randomUUID().toString());
        shop.setLan(random.nextDouble());
        shop.setLat(random.nextDouble());
        // TODO: 2017-08-24 这个时间以后测试要用到了再改吧
        shop.setOpenTime(LocalTime.now());
        shop.setCloseTime(LocalTime.now());
        shop.setDeadlineTime(LocalTime.now());
        return shopRepository.saveAndFlush(shop);
    }
}
