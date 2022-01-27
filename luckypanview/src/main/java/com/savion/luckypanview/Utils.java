package com.savion.luckypanview;

import java.util.Collection;
import java.util.Map;

/**
 * Created by goldze on 2017/5/14.
 * 常用工具类
 */
public final class Utils {
    /**
     * @author: savion
     * @date: 2020/12/29 下午 11:14
     * @dess: 列表是否空
     */
    public static boolean isListEmpty(Collection collection) {
        if (collection == null
                || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @author savion
     * @date 2021/8/24
     * @desc 集合是否为空
     **/
    public static boolean isMapEmpty(Map collection) {
        if (collection == null
                || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @author: savion
     * @date: 2020/12/29 下午 11:16
     * @dess: 列表是否包含某一item
     */
    public static <T> boolean isListContain(Collection<T> list, T item) {
        try {
            if (!isListEmpty(list)) {
                return list.contains(item);
            }
        } catch (Exception ignored) {
        }
        return false;
    }


}