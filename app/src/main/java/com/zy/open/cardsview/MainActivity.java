package com.zy.open.cardsview;

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
            "http://img-protected.piaofun.cn/show/73bba199b49e4759b0c259a4e184ff3d/73bba199b49e4759b0c259a4e184ff3d@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/c939005413664de3b234c6d2b7f1961c/c939005413664de3b234c6d2b7f1961c@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/64d6c827766b45769d50fe50356b8739/64d6c827766b45769d50fe50356b8739@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/07c1b60f219c44689e549719345b3131/07c1b60f219c44689e549719345b3131@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/ce95da7c610e46b696ba6ca7eda2be7f/ce95da7c610e46b696ba6ca7eda2be7f@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/29a16cd226a140d1b1d294b6dae4058a/29a16cd226a140d1b1d294b6dae4058a@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/4e40d76357ec46aa9e8da808b3ae4b3c/4e40d76357ec46aa9e8da808b3ae4b3c@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/77aab61aa6a54fb88332959d93b674e5/77aab61aa6a54fb88332959d93b674e5@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/0bb0e6b29c3346b5afddd1d6ce970866/0bb0e6b29c3346b5afddd1d6ce970866@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/a83d2d23889940bab4f4e0758a760e86/a83d2d23889940bab4f4e0758a760e86@450h_600w_0e"
    };

    private String[] imgUrls2 = {
            "http://img-protected.piaofun.cn/show/a2cc073e4d034ec791b318b9ce46ef04/a2cc073e4d034ec791b318b9ce46ef04@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/f36f6b1451bf471a832f61c194953693/f36f6b1451bf471a832f61c194953693@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/35067ed078644b4ea065575f67a9352d/35067ed078644b4ea065575f67a9352d@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/70fce6cd7ccc4fd8b273bc9d385c1216/70fce6cd7ccc4fd8b273bc9d385c1216@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/ad611017dff14ef68f6b029d7da25361/ad611017dff14ef68f6b029d7da25361@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/2bd87de56d594c45a3caa354597e8189/2bd87de56d594c45a3caa354597e8189@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/17646aba1fe64f6eadf59246f13c461a/17646aba1fe64f6eadf59246f13c461a@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/fb94c01728f34438a421a38b5b564b2f/fb94c01728f34438a421a38b5b564b2f@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/44ae50d65be94741ba819929e37e8216/44ae50d65be94741ba819929e37e8216@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/a2dc83f8a86641adbb6b20b6daa1279b/a2dc83f8a86641adbb6b20b6daa1279b@450h_600w_0e"
    };

    private String[] imgUrls3 = {
            "http://img-protected.piaofun.cn/show/ad1ce60f74684d08a19f290a1f32cc38/ad1ce60f74684d08a19f290a1f32cc38@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/0b8ef9a10298433bb60d84077f494897/0b8ef9a10298433bb60d84077f494897@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/d5bfdf9e2bc94cc5973a39a86ea397a3/d5bfdf9e2bc94cc5973a39a86ea397a3@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/8cba8cb0be3f43de800158178d0710b3/8cba8cb0be3f43de800158178d0710b3@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/27a62f69412142b082529f6d0b3491c5/27a62f69412142b082529f6d0b3491c5@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/dca3b3794e594f029136ef91ff2a2e65/dca3b3794e594f029136ef91ff2a2e65@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/6d9abf61e0624a28940736a6d8069a89/6d9abf61e0624a28940736a6d8069a89@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/89502ffd69a84937bf09c138af88e3a9/89502ffd69a84937bf09c138af88e3a9@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/3f7178aa609e44fdb7e0c03c2459f4df/3f7178aa609e44fdb7e0c03c2459f4df@450h_600w_0e",
            "http://img-protected.piaofun.cn/show/0f71d718855843c387dc53f241b1d777/0f71d718855843c387dc53f241b1d777@450h_600w_0e",
    };

    private List<String[]> sources = Arrays.asList(imgUrls2, imgUrls3);
    private int count = 0;

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

    /**
     * 模拟卡片数量少时从服务端获取新数据，这里最多再获取两次，每次十张
     */
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