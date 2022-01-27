package com.savion.luckypanview;

import android.util.Pair;

/**
 * @Author: savion
 * @Date: 2022/1/10 11:46
 * @Des:
 **/
public interface LuckyAlgorithm {

    Pair<Integer, Float> calculation();

    Pair<Integer, Float> calculationPos(Double weight);

    Pair<Integer, Float> calculationPosByRotate(float rotate);
}
