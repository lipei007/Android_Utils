package jack.com.jkutils.infinitephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jack.com.jkutils.R;
import jack.com.jkutils.view.carousel.CarouselView;

public class InfinitePhotoActivity extends AppCompatActivity {

    private final static String ImagesKey = "ImagesKey";
    private final static String OffsetKey = "OffsetKey";

    private final static String PhotoItemKeyPath_URL = "url";
    private final static String PhotoItemKeyPath_Local = "local";
    private final static String PhotoItemKeyPath_PlaceHolder = "placeHolder";

    public static JSONObject buildPhotoItem(String url, boolean local, int placeHolderResId) {
        JSONObject json = new JSONObject();
        try {

            if (url != null) {
                json.put(PhotoItemKeyPath_URL, url);
            }
            json.put(PhotoItemKeyPath_Local,local);
            json.put(PhotoItemKeyPath_PlaceHolder, placeHolderResId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static void startInfinitePhotoActivity(Activity context, InfinitePhotoLoader loader,JSONArray images, int offset) {
        if (context == null || images == null || images.length() == 0) {
            return;
        }

        InfinitePhotoItem.setPhotoLoader(loader);
        Intent intent = new Intent(context, InfinitePhotoActivity.class);
        intent.putExtra(ImagesKey, images.toString());
        intent.putExtra(OffsetKey, offset);

        context.startActivity(intent);
    }

    private CarouselView mCarouselView;
    private TextView mIndicatorTv;
    private ArrayList<InfinitePhotoItem> mPhotoItems;
    private int mOffset;
    private Context mCtx = this;
    private InfinitePhotoActivity self = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jk_infinite_photo_activity);

        mCarouselView = findViewById(R.id.jk_infinite_photo_carousel);
        mIndicatorTv = findViewById(R.id.jk_infinite_photo_indicator_tv);

        Intent intent = getIntent();
        if (intent != null) {

            String imagesStr = intent.getStringExtra(ImagesKey);
            int offset = intent.getIntExtra(OffsetKey, 0);

            try {
                JSONArray images = new JSONArray(imagesStr);
                if (images.length() > 0) {

                    ArrayList<InfinitePhotoItem> tmpArr = new ArrayList<>();
                    for (int i = 0; i < images.length(); i++) {

                        JSONObject itemJson = images.getJSONObject(i);
                        InfinitePhotoItem photoItem = new InfinitePhotoItem(self);
                        photoItem.setValuesForKeysWithJSON(itemJson);
                        tmpArr.add(photoItem);
                    }

                    mPhotoItems = tmpArr;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mOffset = offset;
        }

        updateIndicator();

        // Carousel
        mCarouselView.setAutoScroll(false);
        mCarouselView.registResourceId(R.layout.jk_infinite_photo_cell);
        mCarouselView.setDelegate(new CarouselView.CarouselDelegate() {

            @Override
            public void carouselWillDisplayItem(CarouselView carousel, View cell, int index) {

                Log.d("Carousel", "WillDisplay: " + index + " " + cell);
                updateViewForIndex(cell, index);

            }

            @Override
            public void carouselDidDisplayItem(CarouselView carousel, View cell, int index) {
                Log.d("Carousel", "DidScrollTo: " + index);
                mOffset = index;
                updateIndicator();

                /** 使用数据与Cell绑定后，
                 *  如果图片有两张那么排列位置： 2， 1， 2， 1
                 *  当前位置为2的时候，会预加载位置1、3，那么会将其中一个重置为数据为空。
                 *  滑动到3位置时，会跳转到1位置，此时1位置已经被清空了，所以应当对当前
                 *  显示Cell重新绑定数据
                 * */
                updateViewForIndex(cell, index);
            }

            @Override
            public void carouselWillEndDisplayItem(CarouselView carousel, View cell, int index) {
                Log.d("Carousel", "WillEndDisplay: " + index + " " + cell);
                if (cell instanceof InfinitePhotoItemCell) {
                    InfinitePhotoItemCell photoItemCell = (InfinitePhotoItemCell) cell;
                    photoItemCell.clear();
                }
            }

            @Override
            public int carouselNumberOfItems(CarouselView carousel) {
                if (mPhotoItems == null) {
                    return 0;
                }
                return mPhotoItems.size();
            }
        });
        mCarouselView.reloadDataWithOffset(mOffset);
    }

    /**
     * Private
     * */

    private void updateIndicator() {

        int index = mOffset + 1;
        if (index > mPhotoItems.size()) {
            index = mPhotoItems.size();
        }
        String offsetStr = String.format("%d/%d",index, mPhotoItems.size());
        mIndicatorTv.setText(offsetStr);
    }

    private void updateViewForIndex(View cell, int index) {

        if (cell instanceof InfinitePhotoItemCell) {

            InfinitePhotoItemCell photoItemCell = (InfinitePhotoItemCell) cell;
            photoItemCell.setActivity(self);

            if (index >= mPhotoItems.size()) {
                photoItemCell.setPhotoItem(null);
            } else {
                InfinitePhotoItem photoItem = mPhotoItems.get(index);
                photoItemCell.setPhotoItem(photoItem);
            }
        }
    }
}
