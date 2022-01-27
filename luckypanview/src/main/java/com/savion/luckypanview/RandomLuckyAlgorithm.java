package com.savion.luckypanview;

import android.util.Log;
import android.util.Pair;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * @Author: savion
 * @Date: 2022/1/10 11:47
 * @Des:
 **/
public class RandomLuckyAlgorithm implements LuckyAlgorithm {
    private TreeMap<Double, StaticPanView.StaticPanPojo> weightMap = new TreeMap<>();
    private List<? extends StaticPanView.StaticPanPojo> pojos;
    private int chainStyle = StaticPanView.CHAINSTYLE_SPREAD;

    public RandomLuckyAlgorithm(int chainStyle, List<? extends StaticPanView.StaticPanPojo> pojos) {
        this.chainStyle = chainStyle;
        this.pojos = pojos;
        for (int i = 0; !Utils.isListEmpty(pojos) && i < pojos.size(); i++) {
            //统一转为double
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey();
            //权重累加
            this.weightMap.put(pojos.get(i).luckyWeight() + lastWeight, pojos.get(i));
        }
    }

    @Override
    public Pair<Integer, Float> calculation() {
        if (this.weightMap != null
                && !this.weightMap.isEmpty()) {
            double randomWeight = this.weightMap.lastKey() * Math.random();
            return calculationPos(randomWeight);
        } else {
            return null;
        }
    }

    @Override
    public Pair<Integer, Float> calculationPos(Double weight) {
        SortedMap<Double, StaticPanView.StaticPanPojo> tailMap = this.weightMap.tailMap(weight, true);
        StaticPanView.StaticPanPojo pojo = this.weightMap.get(tailMap.firstKey());
        if (!Utils.isListEmpty(pojos)) {
            //position,rotate
            if (chainStyle == StaticPanView.CHAINSTYLE_SPREAD) {
                //等分
                int position = pojos.indexOf(pojo);
                float singleRotate = 360f / pojos.size();
                float rotate = singleRotate * position + singleRotate / 2f;
                Log.e("savion", String.format("权重均分计算4:%s__%s__%s__%s__%s_%s", weight, position, -rotate, pojo.luckyWeight(), tailMap.lastKey(), tailMap.firstKey()));
                return new Pair<>(position, -rotate);
            } else {
                //权重均分
                int position = pojos.indexOf(pojo);
                if (position > 0) {
                    //当前选中项权重所占角度
                    float currentRotate = (float) (pojo.luckyWeight() * 1f / tailMap.lastKey()) * 360f;
                    float preRotate = (float) (tailMap.firstKey() * 1f / tailMap.lastKey()) * 360f;
                    float rotate = preRotate - currentRotate * 0.5f;
                    Log.e("savion", String.format("权重均分计算1:%s__%s__%s__%s__%s", position, -rotate, pojo.luckyWeight(), tailMap.lastKey(), tailMap.firstKey()));
                    return new Pair<>(position, -rotate);
                } else {
                    float currentRotate = (float) (pojo.luckyWeight() * 1f / tailMap.lastKey()) * 360f;
                    float rotate = currentRotate * 0.5f;
                    Log.e("savion", String.format("权重均分计算2:%s__%s__%s__%s", position, -rotate, pojo.luckyWeight(), tailMap.lastKey()));
                    return new Pair<>(position, -rotate);
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public Pair<Integer, Float> calculationPosByRotate(float rotate) {
        if (weightMap != null) {
            if (chainStyle == StaticPanView.CHAINSTYLE_SPREAD) {
                //均分
                if (!Utils.isListEmpty(pojos)) {
                    float singleRotate = 360f / pojos.size();
                    int position = (int) (rotate / singleRotate);
                    position = Math.max(0, position);
                    position = Math.min(position, pojos.size() - 1);
                    return new Pair<>(position, rotate);
                } else {
                    return null;
                }
            } else {
                //权重均分
                Double weight = weightMap.lastKey() * (rotate / 360f);
                Log.e("savion", String.format("权重均分计算3333:%s__%s__%s__%s", rotate, weight, weightMap.lastKey(), weightMap.firstKey()));
                return calculationPos(weight);
            }
        }
        return null;
    }
}
