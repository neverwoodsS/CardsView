package com.zy.open.cardsview.slide;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.zy.open.cardsview.R;
import com.zy.open.cardsview.util.DisplayUtil;

/**
 * Created by zhangll on 16/5/19.
 */
public class CardView<T extends Card> extends LinearLayout {

    private View rootView;

    private boolean touched;

    private View backView;
    private ViewGroup cardView;
    private View upView;
    private View downView;
    private View noneView;

    private int rotateType = 1;

    private int screenWidth;
    private int screenHeight;

    private T card;

    private CardViewBinder cardViewBinder;
    private View convertView;
    private int layoutRes;

    public CardView(Context context, T card,  @LayoutRes int layoutRes, CardViewBinder<T> cardViewBinder) {
        super(context);
        this.card = card;
        this.layoutRes = layoutRes;
        this.cardViewBinder = cardViewBinder;
        setOrientation(LinearLayout.VERTICAL);
        compute();
        rootView = LayoutInflater.from(context).inflate(R.layout.card_bg, null);
        backView = rootView.findViewById(R.id.back_view);
        cardView = (ViewGroup) rootView.findViewById(R.id.layout_card);
        upView = rootView.findViewById(R.id.view_up);
        downView = rootView.findViewById(R.id.view_down);
        noneView = rootView.findViewById(R.id.view_none);

        //宽取屏幕两倍，高取1.5倍，并居中。 目的：留下足够空间在旋转时不会出现View被截掉的情况
        LayoutParams layoutParams = new LayoutParams(screenWidth * 2, screenHeight * 3 / 2);
        layoutParams.setMargins(-screenWidth / 2,
                -screenWidth / 2,
                0,
                DisplayUtil.dip2px(getContext(), 5));
        addView(rootView, layoutParams);

        RelativeLayout.LayoutParams backParams = (RelativeLayout.LayoutParams) backView.getLayoutParams();
        backParams.width = screenWidth * 2;
        backParams.leftMargin = -screenWidth / 2;
        backView.setLayoutParams(backParams);

        inflateConvertView(LayoutInflater.from(context));
        setListener();
    }

    private void compute() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    private void inflateConvertView(LayoutInflater inflater) {
        convertView = inflater.inflate(layoutRes, null);
        cardView.addView(convertView);
        if (cardViewBinder != null) {
            cardViewBinder.bind(convertView, card);
        }
    }

    private void setListener() {
        //cardView被touch到即卡片被触摸到
        cardView.setOnTouchListener((view, event) -> {
            touched = true;
            return false;
        });

        //卡片上半部分被触摸到
        upView.setOnTouchListener((view, event) -> {
            rotateType = 1;//对旋转方向正修正
            return false;
        });

        //卡片下方1/4被触摸到
        downView.setOnTouchListener((view, event) -> {
            rotateType = -1;//对旋转方向负修正
            return false;
        });

        //卡片中间1/4被触摸到
        noneView.setOnTouchListener((view, event) -> {
            rotateType = 0;//不旋转
            return false;
        });
    }

    public void setCard(T card, int position) {
        this.card = card;
        setTag(card);

//        //如果是启动之后的第一张卡片且不是空卡
//        if (isFirst && position == 0) {
//            isFirst = false;
//            if (card.isEmpty()) {
//                findViewById(R.id.tv_cai).setVisibility(GONE);
//                findViewById(R.id.tv_zan).setVisibility(GONE);
//            } else {
//                findViewById(R.id.tv_cai).setVisibility(VISIBLE);
//                findViewById(R.id.tv_zan).setVisibility(VISIBLE);
//            }
//        } else {
//            findViewById(R.id.tv_cai).setVisibility(GONE);
//            findViewById(R.id.tv_zan).setVisibility(GONE);
//        }
    }

    public T getCard() {
        return card;
    }

    /**
     * 设置卡片旋转角度，但是由拖拽点决定是否正、逆向旋转，或者不旋转
     * @param rotate
     */
    public void setCardRotation(float rotate) {
        cardView.setRotation(rotate * rotateType);
    }

    /**
     * 设置卡片旋转角度，用于放手后动画的效果，方向与拖拽点无关
     * @param rotate
     */
    public void setReleaseRotation(float rotate) {
        cardView.setRotation(rotate);
    }

    public float getCardRotation() {
        return cardView.getRotation();
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public void resetRotateType() {
        rotateType = 1;
    }

    public interface CardViewBinder<T extends Card> {
        void bind(View convertView, T card);
    }
}