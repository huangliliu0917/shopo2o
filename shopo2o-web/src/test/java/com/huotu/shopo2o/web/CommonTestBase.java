package com.huotu.shopo2o.web;

import com.huotu.shopo2o.service.repository.store.StoreRepository;
import com.huotu.shopo2o.web.config.MVCConfig;
import com.huotu.shopo2o.service.config.MallPasswordEncoder;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.store.LngLat;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import org.junit.runner.*;
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
    protected StoreRepository storeRepository;
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

    protected Store mockShop(MallCustomer shopCustomer, MallCustomer customer){
        Store store = new Store();
        store.setId(shopCustomer.getCustomerId());
        store.setCustomer(customer);
        store.setName(UUID.randomUUID().toString());
        store.setAreaCode(UUID.randomUUID().toString());
        store.setTelephone(UUID.randomUUID().toString());
        store.setProvinceCode(UUID.randomUUID().toString());
        store.setCityCode(UUID.randomUUID().toString());
        store.setDistrictCode(UUID.randomUUID().toString());
        store.setAddress(UUID.randomUUID().toString());
        store.setLogo(UUID.randomUUID().toString());
        store.setErpId(UUID.randomUUID().toString());
        store.setLngLat(new LngLat(random.nextDouble(),random.nextDouble()));
        // TODO: 2017-08-24 这个时间以后测试要用到了再改吧
        store.setOpenTime(LocalTime.now());
        store.setCloseTime(LocalTime.now());
        store.setDeadlineTime(LocalTime.now());
        return storeRepository.saveAndFlush(store);
    }
}
