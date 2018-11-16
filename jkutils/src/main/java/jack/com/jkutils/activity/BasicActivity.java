package jack.com.jkutils.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.UUID;

/**
 * 子类需要重写 buildDataProvider 方法，返回 DataProvider 实例。
 * 使用该实例对象持有数据，在OnCreate方法中，savedInstanceState != null
 * 调用刷新界面刷新，在onRestart方法中，调用界面刷新
 *
 * */
public class BasicActivity extends AppCompatActivity {

    private final static String SavedDataProviderKey = "SavedDataProviderKey";

    private static class SaveHandler {

        private HashMap<String, DataProvider> saveMap = new HashMap<>();

        private SaveHandler() {

        }

        private void put(String key, DataProvider dataProvider) {
            saveMap.put(key, dataProvider);
        }

        private void remove(String key) {
            saveMap.remove(key);
        }

        private DataProvider get(String key) {
            return saveMap.get(key);
        }
    }

    private static SaveHandler mSaveHandler = new SaveHandler();

    public DataProvider mDataProvider;
    private String mUUID;

    private static String activityUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            mUUID = savedInstanceState.getString(SavedDataProviderKey);
            mDataProvider = mSaveHandler.get(mUUID);
            mSaveHandler.remove(mUUID);

        } else {

            mUUID = activityUUID();
            mDataProvider = buildDataProvider();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mSaveHandler.remove(mUUID);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SavedDataProviderKey, mUUID);

        if (mDataProvider != null) {
            mSaveHandler.put(mUUID, mDataProvider);
        }
    }

    public DataProvider buildDataProvider() {

        return null;
    }
}
