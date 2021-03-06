package humm.android.api;

import android.os.AsyncTask;

/**
 * Created by josealonsogarcia on 24/11/15.
 */
public class HummTask<T> {

    public enum TaskMode {
        Serial, Parallel
    }

    private CustomAsyncTask mCustomAsyncTask;

    // Class constructor
    public HummTask(Job job) {

        mCustomAsyncTask = new CustomAsyncTask(job);
    }

    public void start() {

//        mCustomAsyncTask.execute();
        mCustomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void start(TaskMode taskMode) {

        switch (taskMode) {
            case Serial:
                mCustomAsyncTask.execute();
                break;
            case Parallel:
            default:
                mCustomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }

    // CustomAsyncTask
    private class CustomAsyncTask extends AsyncTask<Void, Void, T> {

        private Job<T> mJob;
        private Exception mException;

        private CustomAsyncTask(Job job) {

            mJob = job;
        }

        @Override
        protected T doInBackground(Void... params) {

            try {

                return mJob.onStart();

            } catch (Exception e) {

                e.printStackTrace();
                mException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(T type) {

            super.onPostExecute(type);

            // Checking for exceptions
            if (mException == null)
                mJob.onComplete(type);
            else
                mJob.onError(mException);
        }
    }

    // The Job interface
    public interface Job<T> {

        public T onStart() throws Exception;

        public void onComplete(T object);

        public void onError(Exception e);
    }

}
