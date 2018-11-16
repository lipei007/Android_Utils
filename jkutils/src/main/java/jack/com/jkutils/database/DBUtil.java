package jack.com.jkutils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBUtil implements DBHelper.DBHelperListener{

    private static DBUtil dbUtil;
    private SQLiteDatabase db;

    public interface DBInitialBlock {
        ArrayList<String> prepareDatabaseInitial();
    }

    public interface DBUpgradeBlock {
        ArrayList<String> prepareDatabaseUpgrade();
    }

    private DBInitialBlock mInitialBlock;
    @Override
    public ArrayList<String> prepareInitialForDBCreate() {
        if (mInitialBlock != null) {
            return mInitialBlock.prepareDatabaseInitial();
        }
        return null;
    }

    private DBUpgradeBlock mUpgradeBlock;
    @Override
    public ArrayList<String> prepareUpgrade(int oldVersion, int newVersion) {
        if (mUpgradeBlock != null) {
            return mUpgradeBlock.prepareDatabaseUpgrade();
        }
        return null;
    }


    public static DBUtil sharedDBUtil() {
        synchronized (DBUtil.class) {
            if (dbUtil == null) {
                dbUtil = new DBUtil();
            }
        }
        return dbUtil;
    }

    public void openDatabase(Context ctx, String path, int version, boolean wirtable, DBInitialBlock initialBlk, DBUpgradeBlock upgradeBlk) {

        DBHelper openHandler = new DBHelper(ctx,path,null,version);

        mInitialBlock = initialBlk;
        mUpgradeBlock = upgradeBlk;

        openHandler.setListener(this);

        db = openHandler.openDataBase(wirtable);
    }

    public void insert(String table, ContentValues values) {
        db.insert(table,null,values);
    }

    public void execute(ArrayList<String> sqlArr) {
        try {
            db.beginTransaction(); // 开启事务
            for (int i = 0; i < sqlArr.size(); i++) {
                String sql = sqlArr.get(i);
                db.execSQL(sql);
            }
            db.setTransactionSuccessful(); // 设置事务完成
        } finally {
            db.endTransaction(); // 结束事务
        }
    }

    public void execSQL(String sql) {

        db.execSQL(sql);
    }

    public Cursor query(String sql, String[] args) {
        Cursor cur = db.rawQuery(sql,args);
        cur.moveToFirst();
        return cur;
    }

    public void closeDatabase() {
        if (db == null) {
            return;
        }
        if (db.isOpen() == false)
            return;
        db.close();
    }

    public void closeCursor(Cursor cursor) {

        if (cursor == null)
            return;
        if (cursor.isClosed())
            return;
        cursor.close();
    }


}
