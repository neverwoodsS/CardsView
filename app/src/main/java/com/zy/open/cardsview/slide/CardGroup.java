package com.zy.open.cardsview.slide;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import com.zy.open.cardsview.R;
import java.util.HashMap;
import java.util.List;

public class CardGroup<T extends Card> extends RelativeLayout {

    /** 屏幕上同时容纳卡片数量 */
    private static final int CONTAINER_SIZE = 4;
    /** 拖拽时卡片旋转度数最大绝对值 */
    private static final int MAX_ROTATE = 30;
    /** 放手时动画持续时间 */
    private static final int ANIM_DURATION = 250;
    /** 卡片叠加效果所需，卡片之间的高度差 */
    private static final int STEP_HEIGHT = 30;
    /** 卡片叠加效果所需，卡片之间的缩放比差 */
    private static final float STEP_SCALE = 0.10F;

    /** 卡片数据源 */
    private List<T> contentList;

    /** 屏幕宽度 */
    private int screenWidth;
    /** touch down 时的x值 */
    private int downX;
    /** touch down 时的y值 */
    private int downY;
    /** 最近一次touch down后move事件的次数 */
    private int moveCount;
    /** 最近一次touch down的坐标和当前move事件坐标的距离 */
    private int distance;
    /** 卡片拖动时，distance和最大distance的百分比 */
    private float percent;
    /** 第一张卡片是否被抓着 */
    private boolean grabbed;
    /** 是否正在自动滑动 */
    private boolean moving;

    /** 卡片池 */
    private HashMap<Integer, CardView<T>> slideViewMap = new HashMap<>();

    private MoveToLeftListener<T> moveToLeftListener;
    private MoveToRightListener<T> moveToRightListener;
    private ClickCenterListener<T> clickCenterListener;

    private @LayoutRes int layoutRes;
    private CardView.CardViewBinder cardViewBinder;

    public CardGroup(Context context) {
        super(context);
        compute();
    }

    public CardGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        compute();
    }

    private void compute() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
    }

    private void refreshViews() {
        removeAllViews();
        int size = contentList.size() > CONTAINER_SIZE ? CONTAINER_SIZE : contentList.size();//最大只容纳CONTAINER_SIZE数量的卡片
        //倒序插入卡片
        for (int i = size - 1; i >= 0; i--) {
            CardView<T> cardView = new CardView<T>(getContext(), contentList.get(i), layoutRes, cardViewBinder);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //如果卡片数量是满的，则最后一张卡片没有缩放效果，即和它上一张卡片的缩放大小和位置一致
            cardView.setScaleX((1 - (i == CONTAINER_SIZE - 1 ? i - 1 : i) * STEP_SCALE));
            cardView.setPadding(0, STEP_HEIGHT * (i == CONTAINER_SIZE - 1 ? i - 1 : i), 0, 0);

            //将偏移、缩放量存入tag，方便后面计算、复用
            cardView.setTag(R.id.card_margin_top, STEP_HEIGHT * i);
            cardView.setTag(R.id.card_scale, (1- i * STEP_SCALE));

            addView(cardView, layoutParams);
            slideViewMap.put(i, cardView);
        }
    }

    public void setLayoutRes(@LayoutRes int layoutRes) {
        this.layoutRes = layoutRes;
    }

    public void setCardViewBinder(CardView.CardViewBinder cardViewBinder) {
        this.cardViewBinder = cardViewBinder;
    }

    /**
     * 重置卡片数据源，可达到刷新效果
     * @param contentList
     */
    public void setContentList(List<T> contentList) {
        this.contentList = contentList;
        this.slideViewMap.clear();
        refreshViews();
    }

    public T getFirstCard() {
        if (getFirstSlideView() != null) {
            return getFirstSlideView().getCard();
        }
        return null;
    }

    /**
     * 点击点赞按钮
     */
    public void like() {
        if (!moving && getFirstCard() != null && !getFirstCard().isEmpty()) {
            percent = 0;
            getFirstSlideView().setCardRotation(1);
            makeFirstViewLeave(-1, true);
        }
    }

    /**
     * 点击不喜欢按钮
     */
    public void dislike() {
        if (!moving && getFirstCard() != null && !getFirstCard().isEmpty()) {
            percent = 0;
            getFirstSlideView().setCardRotation(-1);
            makeFirstViewLeave(1, true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (contentList == null || contentList.isEmpty() || moving) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) event.getX();
            downY = (int) event.getY();
            grabbed = getFirstSlideView().isTouched();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (grabbed) {
                makeInvalidate(event.getX(), event.getY());
            }
            moveCount++;
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL ||
                event.getAction() == MotionEvent.ACTION_UP) {
            if (grabbed) {
                //x轴方向移动超过5分之一屏则认为已经做出了选择，否则还原卡片，空卡始终还原
                if (Math.abs(downX - event.getX()) > screenWidth / 5 && !getFirstCard().isEmpty()) {
                    makeFirstViewLeave((int) (downX - event.getX()));
                } else {
                    getFirstSlideView().setTouched(false);
                    makeFirstViewReset();
                    makeUnderViewReset(1);
                    makeUnderViewReset(2);
                }

                //如果moveCount不超过2次，认为卡片没有移动过，则是点击事件
                if (moveCount <= 2 && clickCenterListener != null) {
                    clickCenterListener.onCenterClicked(getFirstCard());
                }
            }
            moveCount = 0;
        }
        return true;
    }

    /**
     * touch move事件刷新view
     * @param x
     * @param y
     */
    private void makeInvalidate(float x, float y) {
        distance = (int) Math.sqrt(Math.pow((downX - x), 2) + Math.pow((downY - y), 2));
        float percent = distance * 1.0f / screenWidth * 2;//卡片浮动效果和distance相关，即x、y轴的移动都相关
        float rotatePercent = (x - downX) * 1.0f / screenWidth;//卡片旋转效果只和x轴移动相关

        //百分比值限制
        if (percent > 1) {
            percent = 1;
        }
        if (rotatePercent > 0 && rotatePercent > 1) {
            rotatePercent = 1;
        } else if (rotatePercent < 0 && rotatePercent < -1) {
            rotatePercent = -1;
        }

        //移动、旋转第一张卡片
        CardView firstSlideView = getFirstSlideView();
        firstSlideView.scrollTo((int) (downX - x), (int) (downY - y));
        firstSlideView.setCardRotation(rotatePercent * MAX_ROTATE);

        makeUnderViewInvalidate(1, percent);
        makeUnderViewInvalidate(2, percent);
    }

    /**
     * 根据卡片的tag参数和移动的距离处理浮动效果
     * @param position
     * @param percent
     */
    private void makeUnderViewInvalidate(int position, float percent) {
        this.percent = percent;
        CardView underSlideView = slideViewMap.get(position);
        if (underSlideView != null) {
            int topMargin = (int) underSlideView.getTag(R.id.card_margin_top);
            float scale = (float) underSlideView.getTag(R.id.card_scale);

            int topPadding = (int) (topMargin - STEP_HEIGHT * percent);
            underSlideView.setPadding(0, topPadding, 0, 0);
            underSlideView.setScaleX(scale + STEP_SCALE * percent);
        }
    }

    /**
     * 重置第一张卡片
     */
    private void makeFirstViewReset() {
        CardView firstSlideView = getFirstSlideView();
        firstSlideView.resetRotateType();
        float rotate = firstSlideView.getCardRotation();
        int startX = firstSlideView.getScrollX();
        int startY = firstSlideView.getScrollY();

        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setTarget(firstSlideView);
        animator.setDuration(ANIM_DURATION);
        animator.setInterpolator(new AnticipateOvershootInterpolator());
        animator.addUpdateListener((animation) -> {
            float curRate = (float) animation.getAnimatedValue();
            firstSlideView.scrollTo((int) (startX * curRate), (int) (startY * curRate));
            firstSlideView.setReleaseRotation(curRate * rotate);
        });

        animator.start();
    }

    /**
     * 重置下面的卡片
     * @param position
     */
    private void makeUnderViewReset(int position) {
        CardView underSlideView = slideViewMap.get(position);

        if (underSlideView != null) {
            int topMargin = (int) underSlideView.getTag(R.id.card_margin_top);
            float scale = (float) underSlideView.getTag(R.id.card_scale);
            int topPadding = underSlideView.getPaddingTop();
            float curScale = underSlideView.getScaleX();

            ValueAnimator paddingAnimator = ValueAnimator.ofInt(topPadding, topMargin);
            paddingAnimator.setTarget(underSlideView);
            paddingAnimator.setDuration(ANIM_DURATION);
            paddingAnimator.setInterpolator(new AnticipateOvershootInterpolator());
            paddingAnimator.addUpdateListener((animation) -> {
                int padding = (int) animation.getAnimatedValue();
                underSlideView.setPadding(0, padding, 0, 0);
            });

            ValueAnimator scaleAnimator = ValueAnimator.ofFloat(curScale, scale);
            scaleAnimator.setTarget(underSlideView);
            scaleAnimator.setDuration(ANIM_DURATION);
            scaleAnimator.setInterpolator(new AnticipateOvershootInterpolator());
            scaleAnimator.addUpdateListener(animation -> {
                float valueScale = (float) animation.getAnimatedValue();
                underSlideView.setScaleX(valueScale);
            });

            paddingAnimator.start();
            scaleAnimator.start();
        }
    }

    /**
     * 将第一张卡片旋转滑出屏幕
     */
    private void makeFirstViewLeave(int deltaX) {
        makeFirstViewLeave(deltaX, false);
    }

    /**
     * 是否是外部的命令导致滑动
     * @param deltaX
     * @param isOutOrder
     */
    private void makeFirstViewLeave(int deltaX, boolean isOutOrder) {
        moving = true;
        makeUnderViewReplace(1);
        makeUnderViewReplace(2);

        final CardView firstSlideView = getFirstSlideView();
        final float rotate = firstSlideView.getCardRotation();
        final int startX = firstSlideView.getScrollX();
        final int startY = firstSlideView.getScrollY();

        int endX = deltaX < 0 ? -screenWidth : screenWidth;
        endX = endX * 3 / 2;
        int endRotate = rotate == 0 ? 0 : rotate < 0 ? -45 : 45;

        final int distanceOfX = endX - startX;
        final float deltaRotate = endRotate - rotate;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setTarget(firstSlideView);
        animator.setDuration(ANIM_DURATION + 50);//动画时间+50，保证相对于其他卡片，这个动画是最后一个执行完成的，否则刷新数据源时效果会出问题
        animator.setInterpolator(isOutOrder? new AnticipateInterpolator() : new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float percent = (float) animation.getAnimatedValue();
            firstSlideView.scrollTo((int) (startX + percent * distanceOfX), startY);
            firstSlideView.setReleaseRotation(rotate + deltaRotate * percent);

            //动画执行完成时移出第一张卡片的数据，刷新数据源
            if (percent == 1) {
                T card = contentList.remove(0);
                setContentList(contentList);
                //发送相应通知
                if (deltaX > 0) {
                    if (moveToLeftListener != null) {
                        moveToLeftListener.onMoveToLeft(card);
                    }
                } else {
                    if (moveToRightListener != null) {
                        moveToRightListener.onMoveToRight(card);
                    }
                }
                moving = false;
            }
        });

        animator.start();
    }

    /**
     * 第一张卡片滑出时，剩余卡片上浮
     * @param position
     */
    private void makeUnderViewReplace(int position) {
        CardView underSlideView = slideViewMap.get(position);

        if (underSlideView != null) {
            ValueAnimator animator = ValueAnimator.ofFloat(percent, 1);
            animator.setTarget(underSlideView);
            animator.setDuration(ANIM_DURATION);
            animator.setInterpolator(new AnticipateOvershootInterpolator());
            animator.addUpdateListener(animation -> {
                float curPercent = (float) animation.getAnimatedValue();
                makeUnderViewInvalidate(position, curPercent);
            });

            animator.start();
        }
    }

    public CardView<T> getFirstSlideView() {
        return slideViewMap.get(0);
    }


    public void setMoveToLeftListener(MoveToLeftListener<T> moveToLeftListener) {
        this.moveToLeftListener = moveToLeftListener;
    }

    public void setMoveToRightListener(MoveToRightListener<T> moveToRightListener) {
        this.moveToRightListener = moveToRightListener;
    }

    public void setClickCenterListener(ClickCenterListener<T> clickCenterListener) {
        this.clickCenterListener = clickCenterListener;
    }

    public interface MoveToLeftListener<T extends Card> {
        void onMoveToLeft(T t);
    }

    public interface MoveToRightListener<T extends Card> {
        void onMoveToRight(T t);
    }

    public interface ClickCenterListener<T extends Card> {
        void onCenterClicked(T t);
    }
}