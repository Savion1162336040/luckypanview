package com.savion.luckypanview;

import androidx.databinding.BindingAdapter;

/**
 * @Author: savion
 * @Date: 2020/7/21 13:55
 * @Des:
 **/
public class StaticPanBinding {

    /**
     * @author savion
     * @date 2022/1/28
     * @desc 设置圆盘样式数据
    **/
    @BindingAdapter(value = "static_pan_style_pojo", requireAll = false)
    public static void setPanStylePojo(final StaticPanView staticPanView, final StaticPanView.StaticPanStylePojo pojo) {
        staticPanView.setStaticPanPojo(pojo);
        staticPanView.invalidate();
    }

    /**
     * @author savion
     * @date 2022/1/28
     * @desc 设置圆盘盘片与权重等数据
    **/
    @BindingAdapter(value = "static_pan_pojo", requireAll = false)
    public static void setPanPojo(final StaticPanView staticPanView, final StaticPanView.StaticPanPanProvider pojo) {
        staticPanView.setStaticPanPanProvider(pojo);
        staticPanView.invalidate();
    }



}
