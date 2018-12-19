package jack.com.jkutils.contactKit.element;

import android.net.Uri;

/**
 * 修改Contact Photo时，应当修改thumbnail的值
 * */
public class Photo extends BaseElement {

    public Uri uri;
    public byte[] thumbnail;

    public Photo(Integer id) {
        super(id);
    }
}
