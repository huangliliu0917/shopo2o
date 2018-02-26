package com.huotu.shopo2o.service.entity.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * Brief: 货品表
 * Created by xyr on 2017/10/11.
 */
@Entity
@Table(name = "Mall_Supplier_Products")
@Cacheable(false)
@Getter
@Setter
public class HbmSupplierProducts {
    /**
     * 货品Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Supplier_Product_Id")
    private Integer supplierProductId;

    /**
     * 商品Id
     */
    @Column(name = "Supplier_Goods_Id")
    private int supplierGoodsId;

    /**
     * 货品编码(货号)
     */
    private String bn;

    /**
     * 条码
     */
    private String barcode;

    /**
     * title
     */
    private String title;

    /**
     * 建议销售价区间（minPrice-maxPrice）
     */
    @Column(name = "Min_Price")
    private double minPrice;
    @Column(name = "Max_Price")
    private double maxPrice;
    /**
     * 平台销售价
     */
    @Column(name = "Customer_Price")
    private Double customerPrice;

    /**
     * 成本
     */
    private double cost;

    /**
     * 市场价
     */
    private double mktPrice;

    /**
     * 名称
     */
    private String name;

    /**
     * 重量
     */
    private double weight;

    /**
     * 单位
     */
    private String unit;

    /**
     * 库存
     */
    private int store;
    /**
     * 预占库存
     */
    private int freez;

    /**
     * 规格描述信息
     * demo 红色,S
     * demo ""
     */
    @Column(name = "Pdt_Desc")
    private String pdtDesc;

    /**
     * 规格值信息
     * demo [{"SpecId":1,"SpecValueId":1,"SpecValue":"红色"},{"SpecId":2,"SpecValueId":8,"SpecValue":"S"}]
     * demo []
     */
    private String props;

    /**
     * 最后修改时间
     */
    @Column(name = "Last_Modify")
    private Date lastModify;

    /**
     * 是否上架
     */
    private int marketable;

    /**
     * 分销商审核状态
     */
    private StoreGoodsStatusEnum.CheckStatusEnum status;

    @Column(name = "Mall_Product_Id")
    private Integer mallProductId;

    /**
     * 会员价
     * levelId:price(-1表示空):maxIntegral
     */
    @Column(name = "UserPriceInfo")
    private String userPriceInfo;

    /**
     * 会员积分信息
     * levelid:integral:maxIntegral|levelid:integral:maxIntegral
     */
    @Column(name = "UserIntegralInfo")
    private String userIntegralInfo;

    /**
     * 最低会员价
     */
    @Column(name = "MinUserPrices")
    private Double minUserPrice;

    /**
     * 货品返利个性化,Null->未个性化，>=0个性化
     */
    @Column(name = "DisRebateCustomPercent")
    private Double disRebateCustomPercent;

    /**
     * 获取规格值的ppid
     *
     * @param specList
     * @return
     */
    public String getPPid(List<HbmSpecification> specList) {
        String ppid = "";
        if (props != null && specList != null) {
            JSONArray array = JSON.parseArray(props);
            for(HbmSpecification spec:specList){
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if(obj.getIntValue("SpecId") == spec.getSpecId()){
                        if (ppid.length() > 0) {
                            ppid += "_";
                        }
                        ppid += (obj.getString("SpecId") + "|" + obj.getString("SpecValueId"));
                        break;
                    }
                }

            }
        }
        return ppid;
    }
    /**
     * 根据规格ID获取规格值ID
     */
    public int getSpecValueId(int specId){
        int specValueId = 0;
        if (props != null) {
            JSONArray array = JSON.parseArray(props);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if(obj.getIntValue("SpecId") == specId){
                    return obj.getIntValue("SpecValueId");
                }
            }
        }
        return specValueId;
    }
    /**
     * 根据规格ID获取规格值别名
     */
    public String getSpecValue(int specId){
        String specValueId = "";
        if (props != null) {
            JSONArray array = JSON.parseArray(props);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if(obj.getIntValue("SpecId") == specId){
                    return obj.getString("SpecValue");
                }
            }
        }
        return specValueId;
    }

    public double disUnifiedRebate(){
        if(minUserPrice == null || disRebateCustomPercent == null){
            return 0D;
        }else{
            return minUserPrice * disRebateCustomPercent / 100;
        }
    }
}
