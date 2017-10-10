package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
public class HbmGoodsTypeRepositoryImpl implements HbmGoodsTypeRepositoryCustom {

    @PersistenceContext(unitName = "entityManager")
    private EntityManager entityManager;
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Override
    public List<HbmGoodsType> getLastUsedByStoreId(int storeId) {
        String sql = "SELECT DISTINCT c.* FROM (SELECT top 10 a.* FROM Mall_Supplier_Goods a ORDER BY a.Supplier_Goods_Id DESC) AS b " +
                ", Mall_Goods_Type c WHERE b.Type_Id = c.Type_Id AND b.Store_Id = " + storeId;
        return getTypeList(sql);
    }

    @Override
    public List<HbmGoodsType> getAllUsedByStoreId(int storeId) {
        String sql = "SELECT DISTINCT b.* FROM Mall_Supplier_Goods a " +
                ", Mall_Goods_Type b WHERE a.Type_Id = b.Type_Id AND a.Store_Id = " + storeId;
        return getTypeList(sql);
    }

    private List<HbmGoodsType> getTypeList(String sql){
        List<HbmGoodsType> typeList = jdbcTemplate.query(sql,((rs,num)->{
            HbmGoodsType type = new HbmGoodsType();
            type.setTypeId(rs.getInt("Type_Id"));
            type.setName(rs.getString("Name"));
            type.setDisabled(rs.getBoolean("Disabled"));
            type.setStandardTypeId(rs.getString("Standard_Type_Id"));
            type.setParent(rs.getBoolean("Is_Parent"));
            type.setParentStandardTypeId(rs.getString("Parent_Standard_Type_Id"));
            type.setPath(rs.getString("Path"));
            type.setCustomerId(rs.getShort("Customer_Id"));
            return type;
        }));
        return typeList;
    }
}
