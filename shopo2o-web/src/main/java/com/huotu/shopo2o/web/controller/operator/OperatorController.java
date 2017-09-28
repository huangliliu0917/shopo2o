package com.huotu.shopo2o.web.controller.operator;

import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.author.Operator;
import com.huotu.shopo2o.service.enums.Authority;
import com.huotu.shopo2o.service.service.author.OperatorService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hxh on 2017-09-11.
 */
@Controller
@RequestMapping("/operator")
public class OperatorController {
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/getOperatorList")
    public String getOperatorList(
            @LoginUser MallCustomer mallCustomer,
            Model model) {
        Authority[] authorities = Authority.values();

        List<Operator> operators = operatorService.findByCustomerId(mallCustomer.getCustomerId());

        model.addAttribute("authorities", authorities);
        model.addAttribute("operators", operators);
        return "operator/operator_list";
    }

    @RequestMapping("/{operatorId}")
    @ResponseBody
    public ApiResult showOperators(@PathVariable Long operatorId) {
        Operator operator = operatorService.findById(operatorId);
        if (operator == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, operator);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ApiResult deleteOperator(@RequestParam Long operatorId) {
        operatorService.delete(operatorId);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @RequestMapping("/disabled")
    @ResponseBody
    @Transactional
    public ApiResult disabledOperator(@RequestParam Long operatorId) {
        Operator operator = operatorService.findById(operatorId);
        if (operator == null) {
            return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
        operator.setDisabled(!operator.isDisabled());
        operatorService.save(operator);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 新建并保存操作员信息
     *
     * @param customer
     * @param authorities
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addAndSaveUser(
            @LoginUser MallCustomer customer,
            @RequestParam(required = false, defaultValue = "0") Long operatorId,
            Operator requestOperator, String... authorities) {
        if (operatorId == 0 && operatorService.countByUsername(requestOperator.getUsername()) > 0) {
            return ApiResult.resultWith(ResultCodeEnum.LOGINNAME_NOT_AVAILABLE);
        }
        Operator operator;
        if (operatorId > 0) {
            operator = operatorService.findById(operatorId);
        } else {
            operator = new Operator();
            operator.setPassword(passwordEncoder.encode(requestOperator.getPassword()));
            operator.setCreateTime(new Date());
            operator.setCustomer(customer);
            operator.setCustomerType(customer.getCustomerType());
        }
        operator.setUsername(requestOperator.getUsername());
        operator.setRealName(requestOperator.getRealName());
        operator.setRoleName(requestOperator.getRoleName());
        operator.setMobile(requestOperator.getMobile());
        Set<Authority> authoritySet = new HashSet<>();
        for (String authority : authorities) {
            authoritySet.add(EnumHelper.getEnumType(Authority.class, authority));
        }
        operator.setAuthoritySet(authoritySet);
        operatorService.save(operator);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
