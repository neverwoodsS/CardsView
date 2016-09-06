package com.zy.open.cardsview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zy.open.cardsview.slide.Card;
import com.zy.open.cardsview.slide.CardGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CardGroup<TestCard> cardGroup;
    private List<TestCard> dataSource;

    private String[] imgUrls = {
            "http://img2.3lian.com/2014/c7/12/d/77.jpg",
            "http://pic3.bbzhi.com/fengjingbizhi/gaoqingkuanpingfengguangsheyingps/show_fengjingta_281299_11.jpg",
            "http://e.hiphotos.baidu.com/image/h%3D200/sign=1e8feb8facd3fd1f2909a53a004f25ce/c995d143ad4bd113eff4cf935eafa40f4bfb0551.jpg",
            "http://www.bz55.com/uploads1/allimg/120312/1_120312100435_8.jpg",
            "http://img3.iqilu.com/data/attachment/forum/201308/21/192654ai88zf6zaa60zddo.jpg",
            "http://b.hiphotos.baidu.com/image/h%3D200/sign=9b711189efc4b7452b94b016fffd1e78/3c6d55fbb2fb4316fc06edda24a4462309f7d371.jpg",
            "http://imga1.pic21.com/bizhi/140129/07162/s05.jpg",
            "http://img2.pconline.com.cn/pconline/0706/19/1038447_34.jpg",
            "http://www.1tong.com/uploads/wallpaper/landscapes/206-2-730x456.jpg",
            "http://e.hiphotos.baidu.com/image/h%3D200/sign=5f5941a28344ebf87271633fe9f8d736/2e2eb9389b504fc2e15bc8a4e1dde71190ef6d0e.jpg",
    };

    private String[] imgUrls2 = {
            "http://pic.58pic.com/58pic/13/71/53/93h58PICGX2_1024.jpg",
            "http://pic30.nipic.com/20130606/3642069_004930072000_2.png",
            "http://img04.tooopen.com/images/20131115/sy_47446221336.jpg",
            "http://pic31.nipic.com/20130731/12440028_171838650000_2.jpg",
            "http://pic15.nipic.com/20110731/8022110_162804602317_2.jpg",
            "http://pic23.nipic.com/20120808/10063898_202518409301_2.jpg",
            "http://image.tianjimedia.com/uploadImages/2012/236/878HE0W687U6.jpg",
            "http://pic.qiantucdn.com/58pic/18/13/67/72w58PICshJ_1024.jpg",
            "http://pic76.nipic.com/file/20150823/9448607_122042419000_2.jpg",
            "http://pic71.nipic.com/file/20150707/13559303_233732580000_2.jpg",
    };

    private String[] imgUrls3 = {
            "http://img15.3lian.com/2015/f3/16/d/41.jpg",
            "http://img1.3lian.com/2015/w7/98/d/23.jpg",
            "http://img2.3lian.com/2014/f2/132/d/1.jpg",
            "http://pic.58pic.com/58pic/15/15/35/82758PICdAG_1024.jpg",
            "http://img1.3lian.com/2015/w7/90/d/1.jpg",
            "http://pic25.nipic.com/20121203/213291_135120242136_2.jpg",
            "http://pic25.nipic.com/20121119/6835836_115116793000_2.jpg",
            "http://img.taopic.com/uploads/allimg/110722/9123-110H20K02577.jpg",
            "http://pic4.nipic.com/20090728/1540250_084431037_2.jpg",
            "http://pic21.nipic.com/20120601/6337790_162402095321_2.jpg",
    };

    private List<String[]> sources = Arrays.asList(imgUrls2, imgUrls3);
    private int count = 0;

    private List<String> showedUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new ArrayList<>();
        for (int i = 0; i < imgUrls.length; i++) {
            dataSource.add(new TestCard(imgUrls[i]));
        }

        cardGroup = (CardGroup<TestCard>) findViewById(R.id.card_group);
        if (cardGroup != null) {
            cardGroup.setLayoutRes(R.layout.item_card);

            cardGroup.setCardViewBinder((view, card) -> {
                TestCard testCard = (TestCard) card;
                ImageView contentIv = (ImageView) view.findViewById(R.id.iv_content);

                if (showedUrls.contains(testCard.imgUrl)) {
                    Log.i("test", "有重复的：" + testCard.imgUrl);
                } else {
                    showedUrls.add(testCard.imgUrl);
                }

                Glide.with(MainActivity.this)
                    .load(testCard.imgUrl)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(contentIv);
            });

            cardGroup.setContentList(dataSource);

            cardGroup.setMoveToLeftListener((card) -> checkSize());
            cardGroup.setMoveToRightListener((card) -> checkSize());
        }
    }

    private void checkSize() {
        Log.i("test", "size = " + dataSource.size());
        if (dataSource.size() < 5 && count < sources.size()) {
            String[] source = sources.get(count++);
            for (int i = 0; i < source.length; i++) {
                dataSource.add(new TestCard(source[i]));
            }
            cardGroup.setContentList(dataSource);
        }
    }

    class TestCard implements Card {

        public String imgUrl;

        public TestCard(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
