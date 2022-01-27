package com.savion.luckypanview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;


import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class StaticPanView extends View {
    public interface StaticPanStylePojo {
        /**
         * @author savion
         * @date 2022/1/8 21:26
         * @desc 边框颜色
         **/
        String provideEdgeColor();

        /**
         * @author savion
         * @date 2022/1/8 21:27
         * @desc 轮盘颜色
         **/
        List<String> providePanColors();

    }

    public interface StaticPanPanProvider {
        List<? extends StaticPanPojo> providePans();
    }

    public interface StaticPanPojo {
        float luckyWeight();

        String luckyName();
    }

    //等分,权重计算选中概率
    public static final int CHAINSTYLE_SPREAD = 1;
    //按权重分布,选中概率平均
    public static final int CHAINSTYLE_SPREAD_INSIDE = 2;
    private int chainStyl = CHAINSTYLE_SPREAD;
    //是否在每个圆盘之间画间隔线
    private boolean drawSplitLine = false;
    //圆盘间隔线颜色
    private int drawSplitLineColor = Color.WHITE;


    public static final int DIVIDER_MODE_NONE = 0;
    public static final int DIVIDER_MODE_SINGLE = 1;
    public static final int DIVIDER_MODE_DOUBLE = 2;

    /**
     * @author savion
     * @date 2022/1/10
     * @desc 旋转圈数
     **/
    private static final int TURN_COUNT = 10;
    /**
     * @author savion
     * @date 2022/1/8 21:50
     * @desc 盘片数量
     **/
    private int panCount = 8;
    private StaticPanStylePojo staticPanStylePojo;
    private StaticPanPanProvider staticPanPanProvider;
    private Paint paint;
    private Paint dividerPaint;
    private boolean antialias = false;
    private int dividerColor = Color.BLACK;
    private int edgeColor = Color.WHITE;
    private int[] panColors = null;

    /**
     * @author savion
     * @date 2022/1/8 23:47
     * @desc 描边样式，单条边还是双条边
     **/
    private int dividerMode = DIVIDER_MODE_NONE;
    /**
     * @author savion
     * @date 2022/1/8 23:48
     * @desc 描边尺寸，以整个圆盘宽度比例计算
     * from:0,to:1
     **/
    private float dividerRatio = 0.01f;

    private float panTextXStartRatio = 0.1f;
    private float panTextXEndRatio = 0.9f;
    private int panTextColor = Color.BLACK;
    //文字最大行数
    private int panTextLines = 1;
    private float panTextSize = 50;
    private Drawable emptyMark;
    private Drawable pointer;
    private float pointerWidthRatio = 0.15f;
    private Path drawTextPath;
    private Paint drawTextPaint;
    private int ratioCount = 8;

    public void setStaticPanPanProvider(StaticPanPanProvider staticPanPanProvider) {
        this.staticPanPanProvider = staticPanPanProvider;
        if (staticPanPanProvider != null && !Utils.isListEmpty(staticPanPanProvider.providePans())) {
            panCount = staticPanPanProvider.providePans().size();
            ratioCount = 0;
            for (int i = 0; i < staticPanPanProvider.providePans().size(); i++) {
                ratioCount += staticPanPanProvider.providePans().get(i).luckyWeight();
            }
        }
    }

    public void reset() {
        cancelAnimator();
        rotateAngle = 0;
        invalidate();
    }

    public void setStaticPanPojo(StaticPanStylePojo staticPanPojo) {
        this.staticPanStylePojo = staticPanPojo;
        if (staticPanPojo != null) {
            if (!TextUtils.isEmpty(staticPanPojo.provideEdgeColor())
                    && isColor(staticPanPojo.provideEdgeColor())) {
                try {
                    edgeColor = Color.parseColor(staticPanPojo.provideEdgeColor());
                } catch (Exception e) {
                    edgeColor = Color.WHITE;
                }
            }
            if (staticPanPojo.providePanColors() != null
                    && staticPanPojo.providePanColors().size() > 0) {
                panColors = new int[staticPanPojo.providePanColors().size()];
                for (int i = 0; i < staticPanPojo.providePanColors().size(); i++) {
                    if (!TextUtils.isEmpty(staticPanPojo.providePanColors().get(i))
                            && isColor(staticPanPojo.providePanColors().get(i))) {
                        try {
                            panColors[i] = Color.parseColor(staticPanPojo.providePanColors().get(i));
                        } catch (Exception e) {
                            panColors[i] = Color.WHITE;
                        }
                    }
                }
            }
        }
    }

    public static boolean isColor(String colorPattern) {
        return Pattern.matches("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}|[0-9a-fA-F]{8})$", colorPattern);
    }


    public StaticPanStylePojo getStaticPanStylePojo() {
        return staticPanStylePojo;
    }

    public StaticPanView(Context context) {
        this(context, null);
    }

    public StaticPanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StaticPanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.StaticPanView);
        dividerMode = t.getInt(R.styleable.StaticPanView_static_pan_divider_mode, dividerMode);
        dividerRatio = t.getFloat(R.styleable.StaticPanView_static_pan_divider_ratio, dividerRatio);
        dividerColor = t.getColor(R.styleable.StaticPanView_static_pan_divider_color, dividerColor);
        antialias = t.getBoolean(R.styleable.StaticPanView_static_pan_antialias, antialias);
        emptyMark = t.getDrawable(R.styleable.StaticPanView_static_pan_empty_mark);
        panTextXStartRatio = t.getFloat(R.styleable.StaticPanView_static_pan_draw_text_x_start_ratio, panTextXStartRatio);
        panTextXEndRatio = t.getFloat(R.styleable.StaticPanView_static_pan_draw_text_x_end_ratio, panTextXEndRatio);
        panTextColor = t.getColor(R.styleable.StaticPanView_static_pan_draw_text_color, panTextColor);
        panTextSize = t.getDimension(R.styleable.StaticPanView_static_pan_draw_text_size, panTextSize);
        panTextLines = t.getInt(R.styleable.StaticPanView_static_pan_draw_text_line, panTextLines);
        pointer = t.getDrawable(R.styleable.StaticPanView_static_pan_pointer);
        pointerWidthRatio = t.getFloat(R.styleable.StaticPanView_static_pan_pointer_ratio, pointerWidthRatio);
        chainStyl = t.getInt(R.styleable.StaticPanView_static_pan_chainstyle, chainStyl);
        drawSplitLine = t.getBoolean(R.styleable.StaticPanView_static_pan_draw_split_line, drawSplitLine);
        drawSplitLineColor = t.getColor(R.styleable.StaticPanView_static_pan_draw_split_line_color, drawSplitLineColor);
        t.recycle();
        dividerRatio = NumberUtil.between(dividerRatio, 1f, 0f);

        //最小1行
        panTextLines = Math.max(1,panTextLines);

        drawTextPath = new Path();

        drawTextPaint = new Paint();
        drawTextPaint.setColor(panTextColor);
        drawTextPaint.setTextSize(panTextSize);
        drawTextPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setStyle(Paint.Style.FILL);
        dividerPaint.setColor(drawSplitLineColor);
        dividerPaint.setStrokeWidth(5);

        if (antialias) {
            paint.setAntiAlias(true);
        }
    }

    public void setPanCount(int panCount) {
        this.panCount = panCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (width > 0) {
            int heightM = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int widthM = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            setMeasuredDimension(widthM, heightM);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (staticPanStylePojo != null) {
            if (chainStyl == CHAINSTYLE_SPREAD) {
                drawChainStyleSpread(canvas);
            } else {
                drawChainStyleSpreadInside(canvas);
            }
        } else {
            //画问号
            if (emptyMark != null) {
                float ratio = (Math.max(emptyMark.getIntrinsicWidth() * 1f / (getWidth()), emptyMark.getIntrinsicHeight() * 1f / (getHeight())));
                float toWidth = emptyMark.getIntrinsicWidth() / ratio;
                float toHeight = emptyMark.getIntrinsicHeight() / ratio;
                emptyMark.setBounds((int) (getWidth() / 2f - toWidth / 2f)
                        , (int) (getHeight() / 2f - toHeight / 2f)
                        , (int) (getWidth() / 2f + toWidth / 2f)
                        , (int) (getHeight() / 2f + toHeight / 2f));
                emptyMark.draw(canvas);
            }
        }
    }

    /**
     * @author savion
     * @date 2022/1/19
     * @desc 权重分布
     **/
    private void drawChainStyleSpreadInside(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotateAngle, getWidth() / 2f, getHeight() / 2f);
        //边框宽度
        float dividerWidth = dividerMode != DIVIDER_MODE_NONE ? getWidth() * dividerRatio : 0;
        //圆盘片半径
        float panRadius = getWidth() * 0.45f;
        //画底色
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(edgeColor);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - dividerWidth / 2f, paint);


        if (panColors != null && panColors.length > 0) {
            float singleAngle = 360f / panCount;
            float startAngle = -90;
            float panLeft = getWidth() / 2f - panRadius;
            float panTop = getHeight() / 2f - panRadius;
            float panRight = getWidth() / 2f + panRadius;
            float panBottom = getHeight() / 2f + panRadius;

            //绘制文本xOffset
            float xOffset = panRadius * panTextXStartRatio;
            float drawRadius = (panTextXEndRatio) * panRadius;
            //圆心x
            float centerX = getWidth() / 2f;
            //圆心y
            float centerY = getHeight() / 2f;

            //根据单片角度计算文字大小
            float minDrawHeight = (float) (Math.tan(Math.toRadians(singleAngle / 2f)) * xOffset * 2f) * 0.9f;
            if (minDrawHeight < panTextSize) {
                drawTextPaint.setTextSize(minDrawHeight);
            } else {
                drawTextPaint.setTextSize(panTextSize);
            }

            //Log.e("savion", String.format("绘制文字:%s__%s", minDrawHeight, drawTextPaint.getTextSize()));

            for (int i = 0; i < panCount; i++) {
                float percent = staticPanPanProvider.providePans().get(i).luckyWeight() / ratioCount;
                singleAngle = percent * 360f;
                //从正上方顺时针绘制
                //canvas内0度是从正右方开始的
                paint.setColor(panColors[i % panColors.length]);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawArc(panLeft, panTop, panRight, panBottom,
                        startAngle,
                        singleAngle,
                        true,
                        paint);

                if (staticPanPanProvider != null && !Utils.isListEmpty(staticPanPanProvider.providePans())) {
                    if (i < staticPanPanProvider.providePans().size()) {
                        if (!TextUtils.isEmpty(staticPanPanProvider.providePans().get(i).luckyName())) {
                            //角度
                            float angle = (float) Math.toRadians(startAngle + singleAngle / 2f);
                            //结束点x
                            float x = (float) (centerX + Math.cos(angle) * drawRadius);
                            //结束点y
                            float y = (float) (centerY + Math.sin(angle) * drawRadius);
                            String str = staticPanPanProvider.providePans().get(i).luckyName();
                            drawLineText(canvas, str, panRadius, xOffset, centerX, centerY, x, y);
                        }
                    }
                }
                //Log.e("savion", String.format("绘制弧形:%s__%s", startAngle, singleAngle));
                startAngle += singleAngle;
            }
            //画白边
            if (drawSplitLine) {
                startAngle = -90;
                for (int i = 0; i < panCount; i++) {
                    float percent = staticPanPanProvider.providePans().get(i).luckyWeight() / ratioCount;
                    singleAngle = percent * 360f;
                    //角度
                    float lineAngle = (float) Math.toRadians(startAngle);
                    //结束点x
                    float lineX = (float) (centerX + Math.cos(lineAngle) * panRadius);
                    //结束点y
                    float lineY = (float) (centerY + Math.sin(lineAngle) * panRadius);
                    canvas.drawLine(centerX, centerY, lineX, lineY, dividerPaint);
                    //Log.e("savion", String.format("绘制弧形:%s__%s", startAngle, singleAngle));
                    startAngle += singleAngle;
                }
            }
        }


        canvas.drawPoint(getWidth() / 2f, getHeight() / 2f, paint);
        if (dividerMode == DIVIDER_MODE_SINGLE) {
            //单条
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dividerWidth);
            //画外圈
            paint.setColor(dividerColor);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - dividerWidth, paint);
        } else if (dividerMode == DIVIDER_MODE_DOUBLE) {
            //双条
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dividerWidth);
            //画外圈
            paint.setColor(dividerColor);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - dividerWidth, paint);
            //画内圈
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, panRadius, paint);
        }
        canvas.restore();
        //画指针
        if (pointer != null) {
            float ratio = (Math.max(pointer.getIntrinsicWidth() * 1f / (getWidth() * pointerWidthRatio),
                    pointer.getIntrinsicHeight() * 1f / (getHeight() * pointerWidthRatio)));
            float toWidth = pointer.getIntrinsicWidth() / ratio;
            float toHeight = pointer.getIntrinsicHeight() / ratio;
            pointer.setBounds((int) (getWidth() / 2f - toWidth / 2f)
                    , (int) (getHeight() / 2f - toHeight / 2f)
                    , (int) (getWidth() / 2f + toWidth / 2f)
                    , (int) (getHeight() / 2f + toHeight / 2f));
            pointer.draw(canvas);
        }
    }

    private void drawLineText(Canvas canvas, String str, float panRadius
            , float xOffset
            , float centerX, float centerY
            , float endX, float endY) {
        float drawWidth = (panTextXEndRatio - panTextXStartRatio) * panRadius;

        int indexStart = 0;
        int currentIndex = 0;
        int currentLine = 0;
        String[] lines = new String[panTextLines];
        //分行
        while (indexStart < str.length() && currentLine < panTextLines) {
            String con = str.substring(indexStart);
            currentIndex = drawTextPaint.breakText(con, true, drawWidth, null);
            indexStart += currentIndex;
            lines[currentLine] = con.substring(0, currentIndex);
            ++currentLine;
        }

        if (currentLine > 0) {
            //将文字居中显示
            Paint.FontMetrics fm = drawTextPaint.getFontMetrics();
            float A = Math.abs(fm.ascent);
            float D = Math.abs(fm.descent);
            //单文字总高度
            float H = A + D;
            //文字间隔
            float S = 0;
            //文字总高度
            float TH = H * currentLine + ((currentLine - 1) * S);

            for (int i = 0; i < currentLine; i++) {
                float yOffset = A + i * (H + S) - (TH / 2f);
                if (!TextUtils.isEmpty(lines[i])) {
                    String s = lines[i];
                    if (i == currentLine - 1) {
                        if (s.charAt(s.length() - 1) != str.charAt(str.length() - 1)) {
                            //如果还有下一行，则此行末尾加...
                            s = s.substring(0, s.length() - 1) + "...";
                        }
                    }
                    float singleWidth = drawTextPaint.measureText(s);
                    drawTextPath.reset();
                    drawTextPath.moveTo(centerX, centerY);
                    drawTextPath.lineTo(endX, endY);
                    float newXoffset = xOffset + (drawWidth - singleWidth);
                    //Log.e("savion", String.format("计算文字绘制:%s__%s__%s__%s_%s_%s_%s_%s_%s", newXoffset, yOffset, i, currentLine, A, D, H, S, TH));
                    canvas.drawTextOnPath(s
                            , drawTextPath
                            , newXoffset
                            , yOffset,
                            drawTextPaint);
                }
            }


            //canvas.save();
//            float newCenterY = centerY;
//            float newEndY = endY;
//            for (int i = 0; i < currentLine; i++) {
//                if (!TextUtils.isEmpty(lines[i])) {
//                    String s = lines[i];
//                    if (i == currentLine - 1) {
//                        if (s.charAt(s.length() - 1) != str.charAt(str.length() - 1)) {
//                            //如果还有下一行，则此行末尾加...
//                            s += "...";
//                        }
//                    }
//                    float singleWidth = drawTextPaint.measureText(s);
//                    drawTextPath.reset();
//                    drawTextPath.moveTo(centerX, newCenterY);
//                    drawTextPath.lineTo(endX, newEndY);
//
//                    float newXoffset = xOffset + (drawWidth - singleWidth);
//                    canvas.drawTextOnPath(s
//                            , drawTextPath
//                            , newXoffset
//                            , yOffset,
//                            drawTextPaint);
//
//                    newCenterY += singleHeight + lineSplit;
//                    newEndY += singleHeight + lineSplit;
//                }
//            }
            //canvas.restore();
        }
    }

    /**
     * @author savion
     * @date 2022/1/19
     * @desc 均分
     **/
    private void drawChainStyleSpread(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotateAngle, getWidth() / 2f, getHeight() / 2f);
        //边框宽度
        float dividerWidth = dividerMode != DIVIDER_MODE_NONE ? getWidth() * dividerRatio : 0;
        //圆盘片半径
        float panRadius = getWidth() * 0.45f;
        //画底色
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(edgeColor);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - dividerWidth / 2f, paint);
        if (panColors != null && panColors.length > 0) {
            float singleAngle = 360f / panCount;
            float startAngle = -90;
            float panLeft = getWidth() / 2f - panRadius;
            float panTop = getHeight() / 2f - panRadius;
            float panRight = getWidth() / 2f + panRadius;
            float panBottom = getHeight() / 2f + panRadius;

            //绘制文本xOffset
            float xOffset = panRadius * panTextXStartRatio;
            float drawRadius = (panTextXEndRatio) * panRadius;
            float drawWidth = (panTextXEndRatio - panTextXStartRatio) * panRadius;
            //圆心x
            float centerX = getWidth() / 2f;
            //圆心y
            float centerY = getHeight() / 2f;

            //根据单片角度计算文字大小
            float minDrawHeight = (float) (Math.tan(Math.toRadians(singleAngle / 2f)) * xOffset * 2f) * 0.9f;
            if (minDrawHeight < panTextSize) {
                drawTextPaint.setTextSize(minDrawHeight);
            } else {
                drawTextPaint.setTextSize(panTextSize);
            }
            //Log.e("savion", String.format("绘制文字:%s__%s", minDrawHeight, drawTextPaint.getTextSize()));

            for (int i = 0; i < panCount; i++) {
                //从正上方顺时针绘制
                //canvas内0度是从正右方开始的
                paint.setColor(panColors[i % panColors.length]);
                canvas.drawArc(panLeft, panTop, panRight, panBottom,
                        startAngle,
                        singleAngle,
                        true,
                        paint);

                if (staticPanPanProvider != null && !Utils.isListEmpty(staticPanPanProvider.providePans())) {
                    if (i < staticPanPanProvider.providePans().size()) {
                        if (!TextUtils.isEmpty(staticPanPanProvider.providePans().get(i).luckyName())) {
                            //角度
                            float angle = (float) Math.toRadians(startAngle + singleAngle / 2f);
                            //结束点x
                            float x = (float) (centerX + Math.cos(angle) * drawRadius);
                            //结束点y
                            float y = (float) (centerY + Math.sin(angle) * drawRadius);
                            String str = staticPanPanProvider.providePans().get(i).luckyName();
                            drawLineText(canvas, str, panRadius, xOffset,
                                    centerX, centerY,
                                    x, y);
                        }
                    }
                }
                //Log.e("savion", String.format("绘制弧形:%s__%s", startAngle, singleAngle));
                startAngle += singleAngle;
            }
            //画白边
            if (drawSplitLine) {
                startAngle = -90;
                for (int i = 0; i < panCount; i++) {
                    //角度
                    float lineAngle = (float) Math.toRadians(startAngle);
                    //结束点x
                    float lineX = (float) (centerX + Math.cos(lineAngle) * panRadius);
                    //结束点y
                    float lineY = (float) (centerY + Math.sin(lineAngle) * panRadius);
                    canvas.drawLine(centerX, centerY, lineX, lineY, dividerPaint);
                    //Log.e("savion", String.format("绘制弧形:%s__%s", startAngle, singleAngle));
                    startAngle += singleAngle;
                }
            }
        }

        canvas.drawPoint(getWidth() / 2f, getHeight() / 2f, paint);

        if (dividerMode == DIVIDER_MODE_SINGLE) {
            //单条
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dividerWidth);
            //画外圈
            paint.setColor(dividerColor);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - dividerWidth, paint);
        } else if (dividerMode == DIVIDER_MODE_DOUBLE) {
            //双条
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dividerWidth);
            //画外圈
            paint.setColor(dividerColor);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - dividerWidth, paint);
            //画内圈
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, panRadius, paint);
        }
        canvas.restore();
        //画指针
        if (pointer != null) {
            float ratio = (Math.max(pointer.getIntrinsicWidth() * 1f / (getWidth() * pointerWidthRatio),
                    pointer.getIntrinsicHeight() * 1f / (getHeight() * pointerWidthRatio)));
            float toWidth = pointer.getIntrinsicWidth() / ratio;
            float toHeight = pointer.getIntrinsicHeight() / ratio;
            pointer.setBounds((int) (getWidth() / 2f - toWidth / 2f)
                    , (int) (getHeight() / 2f - toHeight / 2f)
                    , (int) (getWidth() / 2f + toWidth / 2f)
                    , (int) (getHeight() / 2f + toHeight / 2f));
            pointer.draw(canvas);
        }
    }

    public void setAnimationEndListener(AnimationEndListener animationEndListener) {
        this.animationEndListener = animationEndListener;
    }

    private AnimationEndListener animationEndListener;
    private ValueAnimator animtor;
    private int rotateAngle = 0;

    private void cancelAnimator() {
        if (animtor != null) {
            animtor.cancel();
        }
    }

    public void onPause() {
        reset();
    }

    public void onDestory() {
        reset();
    }

    /**
     * @author savion
     * @date 2022/1/10
     * @desc 开始随机抽奖
     **/
    public void startRotate() {
        cancelAnimator();
        //计算随机值
        RandomLuckyAlgorithm algorithm = new RandomLuckyAlgorithm(chainStyl, staticPanPanProvider.providePans());
        Pair<Integer, Float> pair = algorithm.calculation();
        if (pair != null && pair.first >= 0 && pair.first < staticPanPanProvider.providePans().size()) {
//            float singlePanAngle = 360f / panCount;
//            int angle = (int) (-(pair.first * singlePanAngle + (singlePanAngle / 2f)));
//            Log.e("savion", String.format("权重均分计算:旋转角度:%s__%s__%s__%s", pair.first, pair.second, angle, singlePanAngle));
//            animtor = ValueAnimator.ofInt(rotateAngle, angle + 360 * TURN_COUNT);
            animtor = ValueAnimator.ofInt(rotateAngle, (int) (pair.second + 360 * TURN_COUNT));
            animtor.setInterpolator(new AccelerateDecelerateInterpolator());
            animtor.setDuration(5000L);
            animtor.addUpdateListener(animation -> {
                int updateValue = (int) animation.getAnimatedValue();
                rotateAngle = updateValue % 360;
                //int pos = calPos(rotateAngle);
                Pair<Integer, Float> rotatePair = algorithm.calculationPosByRotate(360 - rotateAngle);
                if (rotatePair != null
                        && animationEndListener != null
                        && !Utils.isListEmpty(staticPanPanProvider.providePans())
                        && rotatePair.first < staticPanPanProvider.providePans().size()) {
                    animationEndListener.renderAnimation(rotatePair.first, staticPanPanProvider.providePans().get(rotatePair.first));
                }
                ViewCompat.postInvalidateOnAnimation(this);
            });

            animtor.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (animationEndListener != null
                            && !Utils.isListEmpty(staticPanPanProvider.providePans())
                            && pair.first < staticPanPanProvider.providePans().size()) {
                        animationEndListener.endAnimation(pair.first, staticPanPanProvider.providePans().get(pair.first));
                    }
                }

            });
            animtor.start();
        } else {
            //CatToast.getInstance().showShort(App.getInstance().getString(R.string.error_data));
            Toast.makeText(getContext(),"error data",Toast.LENGTH_SHORT).show();
        }
    }

//    private int calPos(int angle) {
//        if (chainStyl == CHAINSTYLE_SPREAD) {
//            //所有均分
//            angle = angle % 360;
//            int pos = (panCount - 1) - (angle / (360 / panCount));
//            pos = Math.max(0, pos);
//            pos = Math.min(panCount, pos);
//            return pos;
//        } else {
//            //权重均分
//
//        }
//    }

    public interface AnimationEndListener {
        void endAnimation(int position, StaticPanPojo panPojo);

        void renderAnimation(int position, StaticPanPojo panPojo);
    }
}
