package com.huotu.shopo2o.service.service.user.impl;

import com.huotu.shopo2o.service.entity.user.UserBaseInfo;
import com.huotu.shopo2o.service.repository.user.UserBaseInfoRepository;
import com.huotu.shopo2o.service.service.user.UserBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hxh on 2017-09-14.
 */
@Service
public class UserBaseInfoServiceImpl implements UserBaseInfoService{
    @Autowired
    private UserBaseInfoRepository userBaseInfoRepository;
    @Override
    public UserBaseInfo findById(Integer id) {
        return userBaseInfoRepository.findOne(id);
    }
}
