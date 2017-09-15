package com.huotu.shopo2o.service.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
@Entity
@Table(name = "ST_Menu")
@Setter
@Getter
public class StoreMenu {
    @Id
    @Column(name = "MenuId")
    private String menuId;
    @ManyToOne
    @JoinColumn(referencedColumnName = "MenuId")
    private StoreMenu parent;
    @Column(name = "MenuName")
    private String menuName;
    @Column(name="LinkUrl")
    private String linkUrl;
    @Column(name = "SortNum")
    private int sortNum;
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("sortNum asc ")
    private List<StoreMenu> children;
    @Column(name = "Length")
    private int length;
    @Column(name = "Author")
    private String author;
    /**
     * 是否启用,0表示启用,1表示不启用
     */
    @Column(name = "IsDisabled")
    private int isDisabled;
}
