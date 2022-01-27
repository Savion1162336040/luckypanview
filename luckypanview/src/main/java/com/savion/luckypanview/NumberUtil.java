package com.savion.luckypanview;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 数字显示管理工具
 * Created by lixam on 2016/6/12.
 */
public class NumberUtil {
    public static double change(double a) {
        return a * Math.PI / 180;
    }

    public static double changeAngle(double a) {
        return a * 180 / Math.PI;
    }

    /**
     * 将数字转换成友好的显示格式
     * 转换规则：如果超过一万，用单位表示，如10500，显示为1万，小于1万，直接返回原数字
     *
     * @param num 需要转换显示的数字
     * @return
     */
    public static String formatNumToFriendly(int num) {
        DecimalFormat df = new DecimalFormat("###.0");
        if (num < 10000) {
            return num + "";
        } else if (num <= 10000 * 10000) {
            return df.format(num / 10000.0f) + "万";
        } else {
            return df.format(num / 10000.0f / 10000.0f) + "亿";
        }
    }

    /**
     * 将字符串转换成 double
     *
     * @param doubleStr
     * @return
     */
    public static double strToDouble(String doubleStr) {
        try {
            if (doubleStr != null && !doubleStr.equals("")) {
                double temp = Double.parseDouble(doubleStr);
                return temp;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @author savion
     * @date 2021/8/24
     * @desc 将字符串转换成Boolean
     **/
    public static boolean strToBoolean(String booleanStr, boolean defaults) {
        try {
            if (!TextUtils.isEmpty(booleanStr)) {
                return Boolean.parseBoolean(booleanStr);
            } else {
                return defaults;
            }
        } catch (Exception e) {
            return defaults;
        }
    }


    /**
     * 将字符串转换成 浮点型
     *
     * @param floatStr
     * @return
     */
    public static float strToFloat(String floatStr) {
        try {
            if (floatStr != null && !floatStr.equals("")) {
                float temp = Float.parseFloat(floatStr);
                return temp;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 将字符串转换成 浮点型
     *
     * @param floatStr
     * @return
     */
    public static long strToLong(String floatStr) {
        try {
            if (floatStr != null && !floatStr.equals("")) {
                if (floatStr.contains(".")) {
                    return (long) Float.parseFloat(floatStr);
                } else {
                    return Long.parseLong(floatStr);
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将字符串转换成 整型
     *
     * @param intStr
     * @return
     */
    public static int strToInt(String intStr) {
        try {
            if (intStr != null && !intStr.equals("")) {
                int temp = Integer.parseInt(intStr);
                return temp;
            } else {
                return 0;
            }
        } catch (NumberFormatException n) {
            return (int) strToFloat(intStr);
        }
    }

    public static int strToInt(String intStr, int defaults) {
        try {
            if (intStr != null && !intStr.equals("")) {
                int temp = Integer.parseInt(intStr);
                return temp;
            } else {
                return defaults;
            }
        } catch (Exception n) {
            return defaults;
        }
    }

    /**
     * 格式化数值
     *
     * @param value
     * @param scale
     * @return
     */
    public static float formatNum(float value, int scale) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(scale, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }

    /**
     * 格式化数据
     *
     * @param value        数值
     * @param scale        保留小数位
     * @param roundingMode 进位模式
     * @return
     */
    public static float formatPrice(float value, int scale, RoundingMode roundingMode) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(scale, roundingMode);
        return bigDecimal.floatValue();
    }


    /**
     * 浮点型保留两位小数
     *
     * @param num
     * @return
     */
    public static float afterPointTwo(float num) {
        return new BigDecimal(num).setScale(2, BigDecimal.ROUND_HALF_EVEN).floatValue();
    }

    /**
     * 浮点型保留两位小数  舍弃第三位
     *
     * @param num
     * @return
     */
    public static float afterPointTwo1(float num) {
        return new BigDecimal(num).setScale(2, BigDecimal.ROUND_DOWN).floatValue();
    }


    /**
     * 除法，结果保留两位小数，其他位四舍五入
     *
     * @param value1
     * @param value2
     * @param scale
     * @return
     * @throws IllegalAccessException
     */
    public static float div(float value1, float value2, int scale) {
        try {
            //如果精确范围小于0，抛出异常信息
            if (scale < 0) {
                try {
                    throw new IllegalAccessException("精确度不能小于0");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            BigDecimal b1 = new BigDecimal(Float.valueOf(value1));
            BigDecimal b2 = new BigDecimal(Float.valueOf(value2));
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_EVEN).floatValue();
        } catch (Exception e) {
            return value1;
        }
    }

    /**
     * 加
     *
     * @param value1
     * @param value2
     * @return
     */
    public static float add(float value1, float value2) {
        BigDecimal b1 = new BigDecimal(Float.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Float.valueOf(value2));
        return b1.add(b2).setScale(2, BigDecimal.ROUND_HALF_EVEN).floatValue();
    }

    /**
     * 减
     *
     * @param value1
     * @param value2
     * @return
     */
    public static float sub(float value1, float value2) {
        BigDecimal b1 = new BigDecimal(Float.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Float.valueOf(value2));
        return b1.subtract(b2).setScale(2, BigDecimal.ROUND_HALF_EVEN).floatValue();
    }


    /**
     * 乘
     *
     * @param value1
     * @param value2
     * @return
     */
    public static float mul(float value1, Float value2) {
        BigDecimal b1 = new BigDecimal(Float.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Float.valueOf(value2));
        return b1.multiply(b2).setScale(2, BigDecimal.ROUND_HALF_EVEN).floatValue();
    }

    /**
     * 算计被除数能被另一个数整除的最接近的数,如被除数1080,除数16，不能被带除，最接近的能被整除的是1088
     *
     * @param from 被除数
     * @param to   除数
     * @return
     */
    public static int nearestTo(int from, int to) {
//        int mod = from % to;
//        if (mod != 0) {
//            //大于等于除数一半则向上取最接近的数，小于则向下取最接近的数
//            return (to - mod) <= (to / 2f) ? from + mod : from - mod;
//        } else {
//            return from;
//        }
        return nearestDownTo(from, to);
    }

    /**
     * 向上取最接近的值
     *
     * @return
     */
    public static int nearestUpTo(int from, int to) {
        int mod = from % to;
        if (mod != 0) {
            //大于等于除数一半则向上取最接近的数，小于则向下取最接近的数
            return from + mod;
        } else {
            return from;
        }
    }

    /**
     * 向下取最接近的值
     *
     * @return
     */
    public static int nearestDownTo(int from, int to) {
        int mod = from % to;
        if (mod != 0) {
            //大于等于除数一半则向上取最接近的数，小于则向下取最接近的数
            //return (to - mod) <= (to / 2f) ? from + mod : from - mod;
            return from - mod;
        } else {
            return from;
        }
    }


    public static float between(float num, float max, float min) {
        return Math.min(Math.max(num, min), max);
    }

}
