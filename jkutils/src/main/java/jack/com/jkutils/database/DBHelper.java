package jack.com.jkutils.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public interface DBHelperListener {

        ArrayList<String> prepareInitialForDBCreate();
        ArrayList<String> prepareUpgrade(int oldVersion, int newVersion);

    }

    private DBHelperListener mListener;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public void setListener(DBHelperListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (mListener != null) {
            ArrayList<String> tableArr = mListener.prepareInitialForDBCreate();
            for (int i = 0; i < tableArr.size(); i++) {
                String sql = tableArr.get(i);
                db.execSQL(sql);
            }
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (mListener != null) {
            ArrayList<String> tableArr = mListener.prepareUpgrade(oldVersion, newVersion);
            for (int i = 0; i < tableArr.size(); i++) {
                String sql = tableArr.get(i);
                db.execSQL(sql);
            }
        }

    }

    public SQLiteDatabase openDataBase(boolean writable) {
        if (writable) {
            return getWritableDatabase();
        } else  {
            return getReadableDatabase();
        }
    }
}
