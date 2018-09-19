package com.mysoft.debug_tools.entity;

/**
 * Created by Zourw on 2018/9/14.
 */
public class SectionEntity<T> extends com.chad.library.adapter.base.entity.SectionEntity<T> {
    public SectionEntity(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public SectionEntity(T t) {
        super(t);
    }
}
