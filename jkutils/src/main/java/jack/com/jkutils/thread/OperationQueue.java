package jack.com.jkutils.thread;

import android.os.AsyncTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OperationQueue {

    /**
     * 根据CPU核心数控制线程数
     * */
    private static final int count = Runtime.getRuntime().availableProcessors() * 3;

    private static ExecutorService limitedTaskExecutor = null;

    private static volatile OperationQueue sharedQueue;

    private OperationQueue() {
        limitedTaskExecutor = Executors.newFixedThreadPool(count);
    }

    public static OperationQueue sharedOperationQueue() {

        if (sharedQueue == null) {

            synchronized (OperationQueue.class) {

                if (sharedQueue == null) {
                    sharedQueue = new OperationQueue();
                }
            }
        }

        return sharedQueue;
    }

    public OperationTask addOperationTask(OperationCallback callback) {

        OperationTask task = new OperationTask(callback);
        task.executeOnExecutor(limitedTaskExecutor);

        return task;
    }

    public class OperationTask extends AsyncTask<Void, Void, Object> {

        private OperationCallback mCallback;

        private OperationTask(OperationCallback callback) {
            mCallback = callback;
        }

        @Override
        protected Object doInBackground(Void... voids) {

            if (mCallback != null) {
                return mCallback.operationDoInBackground();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object object) {

            if (mCallback != null) {
                mCallback.operationCompletion(object);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            if (mCallback != null) {
                mCallback.operationCancelled();
            }
        }
    }

    public interface OperationCallback {

        Object operationDoInBackground();
        void operationCompletion(Object object);
        void operationCancelled();
    }

}
