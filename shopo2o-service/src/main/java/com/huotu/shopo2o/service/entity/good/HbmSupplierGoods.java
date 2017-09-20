package com.huotu.shopo2o.service.entity.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.net.URI;
import java.util.Date;

/**
 * Created by hxh on 2017-09-18.
 */
@Entity
@Table(name = "Mall_Supplier_Goods")
@Cacheable(false)
@Getter
@Setter
@NoArgsConstructor
public class HbmSupplierGoods {
    /**
     * 商品主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Supplier_Goods_Id")
    private Integer supplierGoodsId;
    /**
     * 商品类型ID
     */
    @ManyToOne
    @JoinColumn(name = "Type_Id")
    private HbmGoodsType type;
    /**
     * 品牌ID
     */
    @ManyToOne
    @JoinColumn(name = "Brand_Id")
    private HbmBrand brand;
    /**
     * 供应商ID
     */
    @Column(name = "Supplier_Id")
    private int supplierId;
    /**
     * 缩略图
     */
    @Column(name = "Thumbnail_Pic")
    private String thumbnailPic;
    /**
     * 小图
     */
    @Column(name = "Small_Pic")
    private String smallPic;
    /**
     * 大图
     */
    @Column(name = "Big_Pic")
    private String bigPic;
    /**
     * 简介
     */
    private String brief;
    /**
     * 详细介绍
     */
    private String intro;
    /**
     * 商品描述
     */
    @Column(name = "Sub_Title")
    private String subTitle;
    /**
     * 市场价
     */
    private double mktPrice;
    /**
     * 成本价
     */
    private double cost;
    /**
     * 销售价
     */
    private double price;

    /**
     * 平台销售价
     */
    @Column(name = "Customer_Price")
    private Double customerPrice;
    /**
     * 商品编号
     */
    private String bn;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 是否上架
     */
    private boolean marketable;
    /**
     * 是否无效 删除的商品设置为无效
     * 为1表示无效
     */
    private boolean disabled;
    /**
     * 重量
     */
    private double weight;
    /**
     * 单位
     */
    private String unit;
    /**
     * 总库存
     */
    private int store;
    /**
     * 可用库存，如果商品已上架，读取MallProduct中的store-freez
     */
    @Transient
    private int usableStore;
    /**
     * 规格名序列化
     * demo1 {"1":"颜色","2":"尺码"}
     * demo2 {}
     */
    private String spec;
    /**
     * 货名集合序列化
     * demo1 [{"ProductId":14,"Desc":"红色,S"},{"ProductId":15,"Desc":"红色,M"},{"ProductId":16,"Desc":"红色,L"},{"ProductId":17,"Desc":"红色,XL"}]
     * "[{\"ProductId\":" + mockSupplierProducts.getProductId() + ",\"Desc\":\"M,栗色\"}]"
     */
    @Column(name = "Pdt_Desc")
    private String pdtDesc;
    /**
     * SpecValue 为规格别名
     * 规格内容序列化
     * demo [{"SpecId":1,"ShowType":"text","SpecValue":"红色","SpecValueId":1,"SpecImage":null,"GoodsImageIds":[]},
     * {"SpecId":2,"ShowType":"text","SpecValue":"S","SpecValueId":8,"SpecImage":null,"GoodsImageIds":[]},
     * {"SpecId":2,"ShowType":"text","SpecValue":"M","SpecValueId":9,"SpecImage":null,"GoodsImageIds":[]},
     * {"SpecId":2,"ShowType":"text","SpecValue":"L","SpecValueId":10,"SpecImage":null,"GoodsImageIds":[]}]
     * demo []
     */
    @Column(name = "Spec_Desc")
    private String specDesc;
    /**
     * 最后修改时间
     */
    @Column(name = "Last_Modify")
    private Date lastModify;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 库存警告数
     */
    @Column(name = "Notify_Num")
    private int notifyNum;
    /**
     * 每人限购
     */
    private int limitBuyNum;
    /**
     * 审核状态
     */
    private StoreGoodsStatusEnum.CheckStatusEnum status;
    /**
     * 备注
     */
    private String remark;

    /**
     * 分销商上架后的商品ID
     */
    @Column(name = "Mall_Goods_Id")
    private Integer mallGoodsId;

    /**
     * 运费模板ID
     */
    @JoinColumn(name = "Freight_Template_Id")
    @ManyToOne
    private FreightTemplate freightTemplate;

    /**
     * 供应商店铺分类
     */
    @JoinColumn(name = "Store_Cat_Id")
    @ManyToOne
    private SupShopCat shopCat;

    @Transient
    private URI picUri;

    /**
     * 商品返利百分比
     */
    @Column(name = "DisRebatePercent")
    private Double disRebatePercent;
    /**
     * 货品返利个性化冗余字段
     * demo [{proid:32343,per:20.32}]
     * demo []
     */
    @Column(name = "DisRebateCustomPercent_Desc")
    private String disRebateCustomPercentDesc;

    public HbmGoodsType getType() {
        return type;
    }

    public void setType(HbmGoodsType type) {
        this.type = type;
    }

    /**
     * 是否能提交审核,删除
     *
     * @return
     */
    public boolean editable() {
        //未提交审核/审核失败/已回收 而且disabled为false，允许编辑，提交审核，删除
        if ((status == StoreGoodsStatusEnum.CheckStatusEnum.UNSUBMIT || status == StoreGoodsStatusEnum.CheckStatusEnum.CHECK_FAILED ||
                status == StoreGoodsStatusEnum.CheckStatusEnum.RECYCLED) && !disabled)
            return true;
        else
            return false;
    }

    /**
     * 可操作（回收，修改库存和分类）的上架商品
     */
    public boolean operable() {
        //审核通过/或回收失败的商品 而且disable为false，才允许回收
        if ((status == StoreGoodsStatusEnum.CheckStatusEnum.CHECKED || status == StoreGoodsStatusEnum.CheckStatusEnum.RECYCLE_FAILED) && !disabled)
            return true;
        else
            return false;
    }

    /**
     * 根据规格值ID返回是否选中
     */
    public boolean specChecked(int specValueId) {
        return spec != null && specDesc.indexOf("\"SpecValueId\":" + specValueId + ",") > -1 ? true : false;
    }

    /**
     * 根据规格值ID获取规格值别名
     */
    public String getSpecValue(int specValueId) {
        String specValue = "";
        if (specDesc != null) {
            JSONArray array = JSON.parseArray(specDesc);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getIntValue("SpecValueId") == specValueId) {
                    return obj.getString("SpecValue");
                }
            }
        }
        return specValue;
    }

    /**
     * 根据规格值ID获取图片列表
     */
    public String getSpecGoodsImageIds(int specValueId) {
        String images = "";
        if (specDesc != null) {
            JSONArray array = JSON.parseArray(specDesc);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getIntValue("SpecValueId") == specValueId) {
                    if (obj.getString("GoodsImageIds") != null && obj.getString("GoodsImageIds").length() > 0) {
                        JSONArray imageArray = JSON.parseArray(obj.getString("GoodsImageIds"));
                        for (int j = 0; j < imageArray.size(); j++) {
                            if (imageArray.getString(j) != null && imageArray.getString(j).length() > 0) {
                                if (images.length() > 0) {
                                    images += "^";
                                }
                                images += ("0|" + imageArray.getString(j));
                            }
                        }
                    }
                    break;
                }
            }
        }
        return images;
    }
}
