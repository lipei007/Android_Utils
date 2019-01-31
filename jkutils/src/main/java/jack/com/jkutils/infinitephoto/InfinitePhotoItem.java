package jack.com.jkutils.infinitephoto;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;


import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

import jack.com.jkutils.image.ImageUtils;
import jack.com.jkutils.object.BasicObject;
import jack.com.jkutils.thread.OperationQueue;

public   class InfinitePhotoItem extends BasicObject {

    public final static int PlaceHolderResIdNone = -1;

    private static InfinitePhotoLoader mLoader;

    public static void setPhotoLoader(InfinitePhotoLoader loader) {
        InfinitePhotoItem.mLoader = loader;
    }

    public String url;
    public boolean local;
    public Bitmap image;
    private WeakReference<InfinitePhotoItemUIDelegate> mWeakdelegate;
    private InfinitePhotoItem self = this;

    private WeakReference<Activity> mWeakCtx;
    public InfinitePhotoItem(Activity context) {
        if (context != null) {
            mWeakCtx = new WeakReference<>(context);
        } else {
            mWeakCtx = null;
        }
    }

    @Override
    public void setValuesForKeysWithJSON(JSONObject json) {
        super.setValuesForKeysWithJSON(json);

        fetchPhoto();
    }

    private InfinitePhotoItemUIDelegate getDelegate() {
        if (mWeakdelegate == null) {
            return null;
        } else {
            return mWeakdelegate.get();
        }
    }

    private void setWeakDelegate(InfinitePhotoItemUIDelegate delegate) {
        if (delegate == null) {
            mWeakdelegate = null;
        } else {
            mWeakdelegate = new WeakReference<>(delegate);
        }
    }

    public void setImage(Bitmap image) {
        this.image = image;

        InfinitePhotoItemUIDelegate oldDelegate = getDelegate();
        if (oldDelegate != null) {
            oldDelegate.refreshUI();
        }
    }

    public Bitmap getImage() {
        return image;
    }

    public void setDelegate(InfinitePhotoItemUIDelegate delegate) {
        InfinitePhotoItemUIDelegate oldDelegate = getDelegate();
        if (oldDelegate != null) {
            oldDelegate.unbind();
        }

        setWeakDelegate(delegate);
    }

    private void fetchPhoto() {
        OperationQueue.sharedOperationQueue().addOperationTask(new OperationQueue.OperationCallback() {
            @Override
            public Object operationDoInBackground() {

                Bitmap photo = null;
                if (url != null) {

                    if (local) {

                        photo = ImageUtils.bitmapFromFile(new File(url));

                    } else {

                        if (mLoader != null) {
                            photo = mLoader.loadImageFromURL(url);
                        }
                    }

                }

                Log.d("Load Photo", "operationDoInBackground: " + photo);
                return photo;
            }

            @Override
            public void operationCompletion(Object object) {

                if (object != null && object instanceof Bitmap) {

                    setImage((Bitmap) object);

                } else {
                    setImage(null);
                }
            }

            @Override
            public void operationCancelled() {

            }
        });
    }
}
