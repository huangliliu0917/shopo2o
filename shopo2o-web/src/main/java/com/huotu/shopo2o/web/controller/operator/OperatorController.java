package com.huotu.shopo2o.web.controller.operator;

import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.author.SupOperator;
import com.huotu.shopo2o.service.enums.Authority;
import com.huotu.shopo2o.service.service.author.SupOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    private SupOperatorService supOperatorService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/getOperatorList")
    public String outputRightMenu(
            @AuthenticationPrincipal MallCustomer mallCustomer,
            Model model) {
        Authority[] authorities = Authority.values();

        List<SupOperator> operators = supOperatorService.findByCustomerId(mallCustomer.getCustomerId());

        model.addAttribute("authorities", authorities);
        model.addAttribute("operators", operators);
        return "operator/operator_list";
    }

    @RequestMapping(value = "/{operatorId}")
    @ResponseBody
    public ApiResult showOperators(@PathVariable Long operatorId) {
        SupOperator supOperator = supOperatorService.findById(operatorId);
        if (supOperator == null) {
            return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, supOperator);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ApiResult deleteOperator(Long operatorId) {
        supOperatorService.delete(operatorId);
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
            @AuthenticationPrincipal MallCustomer customer,
            Long operatorId,
            SupOperator requestOperator, String... authorities) {
        if (operatorId == 0 && supOperatorService.countByUsername(requestOperator.getUsername()) > 0) {
            return ApiResult.resultWith(ResultCodeEnum.LOGINNAME_NOT_AVAILABLE);
        }
        SupOperator operator;
        if (operatorId > 0) {
            operator = supOperatorService.findById(operatorId);
        } else {
            operator = new SupOperator();
            operator.setPassword(passwordEncoder.encode(requestOperator.getPassword()));
            operator.setCreateTime(new Date());
            operator.setCustomer(customer);
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
        supOperatorService.save(operator);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
