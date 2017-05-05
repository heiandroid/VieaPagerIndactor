package sdx.viewpagerindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdx on 2017/5/3.
 * 标题
 */

public class ThinkTitleView extends AppCompatTextView {
    private final String TAG = this.getClass().getSimpleName();
    private Paint textPaint;
    private Paint textEngPaint;
    //轮廓
    private Path mOutLinePath;
    //白色小三角
    private Path mPath;
    private Paint mOutLinePaint;
    private Paint mPaint;
    private Rect mBound;
    //即将显示的textView bound
    private Rect mBoundNext;
    private Rect mBoundEngNext;
    //英文的的rect
    private Rect mEngBound;
    private Paint.FontMetrics fontMetrics;
    private float right_image_margin;
    private BitmapDrawable leftBitmapDrawable;
    private IndicatorOnPageChangeListener mPageChangeListener;
    private String lastTitle;
    //
    private String currentTitle;
    //半透明背景
    private Paint colorPaint;
    private List<String> title = new ArrayList<>();
    private List<String> engTitle = new ArrayList<>();
    private List<Integer> startColor = new ArrayList<>();
    private List<Integer> endColor = new ArrayList<>();
    private ViewPager mViewPager;
    //三角形的宽度
    private float triangleWidth;
    private float minWidth = -1.0f;
    private LinearGradient gradient;
    private ValueAnimator valueAnimator;
    //白色三角形的高度
    private int mHeight;
    //白色三角形的宽度
    private int mWidth;
    private ValueAnimator triAnimation;
    //英文颜色
    private int paintEngColor = Color.WHITE;
    //动画执行的时间
    private int duration = 1000;

    public ThinkTitleView(Context context) {
        this(context, null);
    }

    public ThinkTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThinkTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThinkTitleView);
        final int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            final int index = typedArray.getIndex(i);
            switch (index) {
                case R.styleable.ThinkTitleView_right_image_margin:
                    right_image_margin = typedArray.getDimension(index, 5);
                    break;
                case R.styleable.ThinkTitleView_left_image_src:
                    final Drawable drawableLeft = typedArray.getDrawable(index);
                    if (drawableLeft instanceof BitmapDrawable) {
                        leftBitmapDrawable = (BitmapDrawable) drawableLeft;
                    }
                    break;
                case R.styleable.ThinkTitleView_triangle_width:
                    triangleWidth = typedArray.getDimension(index, 50);
                    break;
                case R.styleable.ThinkTitleView_min_width:
                    minWidth = typedArray.getFloat(index, minWidth);
                    break;
            }
        }
        typedArray.recycle();
        init();
    }


    private void init() {
        textPaint = new TextPaint();
        textEngPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setColor(getCurrentTextColor());
        textPaint.setTextSize(getTextSize());
        textEngPaint.set(textPaint);
        textEngPaint.setTextAlign(Paint.Align.CENTER);
        // TODO: 2017/5/5  设置字体的字体
//        AssetManager mgr = getResources().getAssets();
//        Typeface tf = Typeface.createFromAsset(mgr, "fonts/ThinkTankDate.ttf");
//        textEngPaint.setTypeface(tf);
        colorPaint = new TextPaint();
        colorPaint.setStyle(Paint.Style.FILL);
        colorPaint.setColor(getResources().getColor(R.color.translate_10));
        colorPaint.setAntiAlias(true);
        mHeight = 10;
        mWidth = 20;
        mBound = new Rect();
        mEngBound = new Rect();
        mBoundNext = new Rect();
        mBoundEngNext = new Rect();
    }


    public void setViewPager(ViewPager viewPager, List<Data.DataBean.ItemBean> item) {
        mViewPager = viewPager;
        if (mPageChangeListener != null) {
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
        }
        if (viewPager != null) {
            mViewPager = viewPager;
            if (mPageChangeListener == null) {
                mPageChangeListener = new IndicatorOnPageChangeListener(this);
            }
            mViewPager.addOnPageChangeListener(mPageChangeListener);
            title.clear();
            engTitle.clear();
            startColor.clear();
            endColor.clear();
            for (int i = 0; i < item.size(); i++) {
                title.add(item.get(i).name);
                engTitle.add(item.get(i).engname);
                startColor.add(Color.parseColor(item.get(i).start_color.replace("#", "#CC")));
                endColor.add(Color.parseColor(item.get(i).end_color.replace("#", "#CC")));
            }
        } else {
            Log.e(TAG, "请初始化viewpager");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //以中心开始...
        textPaint.setTextAlign(Paint.Align.CENTER);
        if (TextUtils.isEmpty(currentTitle)) {
            currentTitle = getTitleText(currentItem);
        }
        fontMetrics = textPaint.getFontMetrics();

        textPaint.getTextBounds(currentTitle, 0, currentTitle.length(), mBound);
        textEngPaint.getTextBounds(getEngText(currentItem), 0, getEngText(currentItem).length(), mEngBound);
        if (mOutLinePath == null) {
            mOutLinePath = new Path();
        }
//        currentStart = getCurrentTextStart();
        gradient = new LinearGradient(0, 0, w, 0,
                startColor.get(currentItem), endColor.get(currentItem),
                Shader.TileMode.MIRROR);
        mOutLinePaint = new Paint();
        mOutLinePaint.setStyle(Paint.Style.FILL);
        mOutLinePaint.setAntiAlias(true);
        mOutLinePath.reset();
        mOutLinePath.moveTo(0, 0);
        mOutLinePath.lineTo(w - triangleWidth, 0);
        mOutLinePath.lineTo(w, h);
        mOutLinePath.lineTo(0, h);
        mOutLinePath.close();

        if (mPath == null) {
            mPath = new Path();
        }
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                final float x = event.getX();
                if (x < getLeftWidth()) {
                    if (backListener != null) {
                        backListener.onClick(this);
                    }
                } else {
                    if (listener != null) {
                        listener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }

    public void setOnclickBackListener(OnClickListener l) {
        backListener = l;
    }

    private OnClickListener backListener;
    private OnClickListener listener;

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        listener = l;
    }

    private int currentItem;

    //跳到指定的条目
    public void setCurrentItem(int item) {
        currentItem = item;
        if (mViewPager != null) {
            mViewPager.setCurrentItem(item);
        }
    }

    private float currentStart;

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mOutLinePaint.setShader(gradient);
        textEngPaint.setColor(paintEngColor);
        if (mOutLinePath != null) {
            canvas.drawPath(mOutLinePath, mOutLinePaint);
        }
        if (!TextUtils.isEmpty(currentTitle)) {
            //当前的textView
            canvas.drawText(currentTitle, currentStart, getBaseLineY(), textPaint);
        }
        canvas.clipPath(mOutLinePath);
        //即将显示的text
        if (!TextUtils.isEmpty(lastTitle)) {
            canvas.drawText(lastTitle, getNextTexStart(), getBaseLineY(), textPaint);
        }
        //返回
        if (leftBitmapDrawable != null) {
            Bitmap leftBitmap = leftBitmapDrawable.getBitmap();
            if (leftBitmap != null) {
                canvas.drawRect(0, 0, getPaddingLeft() * 2 + leftBitmap.getWidth(),
                        getMeasuredHeight(), colorPaint);
                canvas.drawBitmap(leftBitmap, getPaddingLeft(), getMeasuredHeight() / 2 - leftBitmap.getHeight() / 2, null);
            }
        }
        //绘制三角符号
        mPath.reset();
        canvas.save();
        if (isVisibleTri) {
            canvas.rotate(currentRe, getTriX() + mWidth / 2, getMeasuredHeight() / 2);
            mPath.moveTo(getTriX(), getMeasuredHeight() / 2 - mHeight / 2);
            mPath.lineTo(getTriX() + mWidth / 2, getMeasuredHeight() / 2 + mHeight / 2);
            mPath.lineTo(getTriX() + mWidth, getMeasuredHeight() / 2 - mHeight / 2);
            canvas.drawPath(mPath, mPaint);
        }
        canvas.restore();
        // : 2017/5/4 根据状态来判断是否显示英文,;
        if (currentStatus != Status.SHORT_STATUS || !isVisibleTri) {
            //需要绘制英文
            canvas.drawText(getEngText(currentItem),
                    currentStart + space + mBound.width() / 2 + mEngBound.width() / 2,
                    getBaseLineY(),
                    textEngPaint);
            canvas.drawText(getEngText(currentItem + 1),
                    getNextEngStart(),
                    getBaseLineY(),
                    textEngPaint);

        } else {

        }
        canvas.save();
        // : 2017/5/5
        canvas.translate(getTranslateX(), 0);
        canvas.drawPath(mPath, mPaint);

    }

    /**
     * 获得画板平移的距离
     */
    private float getTranslateX() {
        if (currentStatus == Status.LONG_STATUS) {
            return getMeasuredWidth() + (mBoundNext.width() / 2 + mBoundEngNext.width() / 2 -
                    mEngBound.width() / 2 - mBound.width() / 2);
        }
        return getMeasuredWidth() + (mBoundNext.width() / 2 - mBound.width() / 2);
    }

    /**
     * 获取即将显示text后边英文的开始位置
     */
    private float getNextEngStart() {
        return getNextTexStart() + space + mBoundNext.width() / 2 + mBoundEngNext.width() / 2;
    }

    /**
     * 获取即将显示的text的开始位置
     */
    private float getNextTexStart() {
        if (currentStatus == Status.LONG_STATUS) {
            Log.e(TAG, "getNextTexStart: long");
            return currentStart + getMeasuredWidth() - (mBoundNext.width() + mBoundEngNext.width() -
                    (mEngBound.width())) / 2 + mBoundNext.width() / 2;

        } else {
            Log.e(TAG, "getNextTexStart: short");
            return currentStart + getMeasuredWidth();
        }
    }

    private float getTriX() {
        if (currentStatus == Status.LONG_STATUS) {
            return currentStart + mBound.width() / 2 + mEngBound.width() + right_image_margin + space;
        }
        return currentStart + mBound.width() / 2 + right_image_margin;
    }

    //滑动viewpager改变渐变和背景色
    private void scrollTextView(int position, float positionOffset, float positionOffsetPixels) {
        currentItem = position;
        //颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        ArgbEvaluator evaluator2 = new ArgbEvaluator();
        int currentStartColor = startColor.get(position);
        int currentEnd = endColor.get(position);
        int targetStart;
        int targetEnd;
        if (position + 1 < startColor.size()) {
            targetStart = startColor.get(position + 1);
            targetEnd = endColor.get(position + 1);
        } else {
            targetStart = currentStartColor;
            targetEnd = currentEnd;
        }

        int evaluate = (Integer) evaluator.evaluate(positionOffset, currentStartColor, targetStart);
        int evaluate2 = (Integer) evaluator2.evaluate(positionOffset, currentEnd, targetEnd);
        gradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                evaluate, evaluate2,
                Shader.TileMode.MIRROR);
        currentTitle = getTitleText(position);
        lastTitle = getTitleText(position + 1);
        final String lastEngTitle = getEngText(position + 1);
        textPaint.getTextBounds(currentTitle, 0, currentTitle.length(), mBound);
        textEngPaint.getTextBounds(getEngText(position), 0, getEngText(position).length(), mEngBound);
        textPaint.getTextBounds(lastTitle, 0, lastTitle.length(), mBoundNext);
        textEngPaint.getTextBounds(lastEngTitle, 0, lastEngTitle.length(), mBoundEngNext);
        FloatEvaluator floatEvaluator = new FloatEvaluator();
        currentStart = floatEvaluator.evaluate(positionOffset, getCurrentTextStart(),
                getCurrentTextStart() - getMeasuredWidth());
        //setting bg paint
        invalidate();
    }

    private float currentRe;

    /**
     * 白色三角做动画
     */
    public void animationTriangle() {
        if (triAnimation != null && triAnimation.isRunning()) return;
        float target;
        float start;
        if (triStatus == TriStatus.DOWN) {
            target = 180;
            start = 0;
            triStatus = TriStatus.UP;
        } else {
            start = 180;
            target = 360;
            triStatus = TriStatus.DOWN;
        }
        triAnimation = ValueAnimator.ofFloat(start, target);
        triAnimation.setDuration(300);
        triAnimation.start();
        triAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentRe = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    public String getEngText(int position) {
        //  2017/5/5 英文名字
        if (engTitle != null && engTitle.size() > position) {
            return engTitle.get(position);
        }
        return "";
    }

    private String getTitleText(int position) {
        if (title != null && title.size() > position) {
            return title.get(position);
        }
        return "";
    }

    private int space = 20;

    private float getCurrentTextStart() {
        if (currentStatus == Status.LONG_STATUS) {
            return (getMeasuredWidth() - triangleWidth - getLeftWidth()) / 2 + getLeftWidth()
                    - (mEngBound.width() - mBound.width() - space) / 2 - mBound.width() / 2 - space;
        }
        return (getMeasuredWidth() - triangleWidth - getLeftWidth()) / 2 + getLeftWidth();
    }

    private int getLeftWidth() {
        if (leftBitmapDrawable != null) {
            final Bitmap bitmap = leftBitmapDrawable.getBitmap();
            if (bitmap != null) {
                return getPaddingLeft() * 2 + bitmap.getWidth();
            }
        }
        return getPaddingLeft() * 2;
    }

    private float getBaseLineY() {
        final float descent = fontMetrics.descent;
        //  2017/5/3
        return getMeasuredHeight() - (getMeasuredHeight() - mBound.height()) / 2 - descent / 2;
    }

    private Status currentStatus = Status.LONG_STATUS;
    private TriStatus triStatus = TriStatus.DOWN;

    /**
     * 获取当前指示器的状态
     *
     * @return 当前的状态
     */
    public Status getStatus() {
        return currentStatus;
    }

    //里边字体开始的位置
    private float startX;
    //字体结束的位置
    private float endX;

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (valueAnimator != null && valueAnimator.isRunning()) return;
        float targetWidth;

        if (currentStatus == Status.LONG_STATUS) {
            //如果没有设置最小宽度,,就缩放50%
            if (minWidth == -1.0f) {
                targetWidth = (float) (getMeasuredWidth() * 0.5);
            } else {
                targetWidth = getMeasuredWidth() * minWidth;
            }

        } else {
            if (minWidth == -1.0f) {
                targetWidth = (float) (getMeasuredWidth() * (1 / 0.5));
            } else {
                targetWidth = getMeasuredWidth() * (1 / minWidth);
            }
        }
        startX = getAnimationStartX(targetWidth);
        endX = getAnimationEndX(targetWidth);
        valueAnimator = ValueAnimator.ofFloat(getMeasuredWidth(), targetWidth);

        valueAnimator.setDuration(duration);
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.width = (int) (float) animation.getAnimatedValue();
                currentStart = startX + (endX - startX) * (valueAnimator.getAnimatedFraction());
                ArgbEvaluator evaluator = new ArgbEvaluator();
                if (currentStatus == Status.LONG_STATUS) {
                    paintEngColor = (int) evaluator.evaluate(valueAnimator.getAnimatedFraction(),
                            Color.WHITE,
                            Color.parseColor("#00FFFFFF"));
                } else {
                    paintEngColor = (int) evaluator.evaluate(valueAnimator.getAnimatedFraction(),
                            Color.parseColor("#00FFFFFF"),
                            Color.WHITE);
                }

                requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setTriVisibility(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setTriVisibility(true);
                if (currentStatus == Status.LONG_STATUS) {
                    currentStatus = Status.SHORT_STATUS;
                } else {
                    currentStatus = Status.LONG_STATUS;
                }
                invalidate();
            }
        });
        valueAnimator.start();
    }

    /**
     * @param targetWidth 目标宽度
     * @return 动画开始的X位置
     */
    private float getAnimationStartX(float targetWidth) {
        if (currentStatus == Status.LONG_STATUS) {
            return getCurrentTextStart();
        } else {
            //  2017/5/4
            return getCurrentTextStart();
        }
    }

    /**
     * @param targetWidth 目标宽度
     * @return 动画结束的X位置
     */
    private float getAnimationEndX(float targetWidth) {
        if (currentStatus == Status.LONG_STATUS) {
            return (targetWidth - triangleWidth - getLeftWidth()) / 2 + getLeftWidth();
        } else {
            return (targetWidth - triangleWidth - getLeftWidth()) / 2 + getLeftWidth()
                    - (mEngBound.width() - mBound.width() - space) / 2 - mBound.width() / 2 - space;

        }
    }


    public enum Status {
        SHORT_STATUS,
        LONG_STATUS
    }

    public enum TriStatus {
        UP,
        DOWN
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (triAnimation != null && triAnimation.isRunning()) {
            triAnimation.cancel();
        }
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }

    //是否显示白色三角,,也可以表示为动画正在执行
    private boolean isVisibleTri = true;

    private void setTriVisibility(boolean isVisible) {
        this.isVisibleTri = isVisible;
    }


    private static class IndicatorOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final WeakReference<ThinkTitleView> indicator;

        public IndicatorOnPageChangeListener(ThinkTitleView indicator) {
            this.indicator = new WeakReference<>(indicator);
        }

        @Override
        public void onPageScrolled(int position, final float positionOffset, final int positionOffsetPixels) {
            final ThinkTitleView myIndicator = indicator.get();
            if (myIndicator != null) {
                myIndicator.scrollTextView(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
