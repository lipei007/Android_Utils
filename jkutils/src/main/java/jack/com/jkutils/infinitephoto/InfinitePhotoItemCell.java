package jack.com.jkutils.infinitephoto;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import java.lang.ref.WeakReference;

import jack.com.jkutils.R;

public class InfinitePhotoItemCell extends RelativeLayout implements InfinitePhotoItemUIDelegate {

    private WeakReference<Activity> mWeakActivity;

    public InfinitePhotoItemCell(Context context) {
        super(context);
    }

    public InfinitePhotoItemCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfinitePhotoItemCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private InfinitePhotoItem mPhotoItem;
    private ImageView mPhotoIv;

    public void setActivity(Activity activity) {

        if (activity == null) {
            mWeakActivity = null;
        } else {
            mWeakActivity = new WeakReference<>(activity);
        }
    }

    public void setPhotoItem(InfinitePhotoItem photoItem) {
        if (mPhotoItem != null) {
            mPhotoItem.setDelegate(null);
        }

        mPhotoItem = photoItem;
        mPhotoItem.setDelegate(this);

        refreshUI();
    }

    @Override
    public void refreshUI() {
        if (mWeakActivity != null) {
            Activity activity = mWeakActivity.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPhotoIv == null) {
                            mPhotoIv = findViewById(R.id.jk_infinite_photo_iv);
                        }

                        Bitmap bitmap = null;
                        if (mPhotoItem != null) {
                             bitmap = mPhotoItem.getImage();
                        }
                        mPhotoIv.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    @Override
    public void unbind() {
        if (mPhotoItem != null) {
            mPhotoItem = null;
        }
        refreshUI();
    }

    public void clear() {
        if (mPhotoItem != null) {
            mPhotoItem.setDelegate(null);
        }
        mPhotoItem = null;
        refreshUI();

    }
}
