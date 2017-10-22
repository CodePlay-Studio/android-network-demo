package my.com.codeplay.training.network_demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkFragment extends Fragment {
    private static final String TAG = NetworkFragment.class.getSimpleName();
    private static final String KEY_URL = "URL";

    private NetworkCallback mCallback;
    private NetworkTask mNetworkTask;
    private String url;

    public static NetworkFragment newInstance(FragmentManager fragmentManager, String url) {
        // Recover NetworkFragment in case the host Activity is re-creating due to a config change.
        NetworkFragment fragment = (NetworkFragment) fragmentManager.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new NetworkFragment();

            Bundle args = new Bundle();
            args.putString(KEY_URL, url);
            fragment.setArguments(args);

            fragmentManager.beginTransaction().add(fragment, TAG).commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes in the host Activity
        setRetainInstance(true);

        url = getArguments().getString(KEY_URL);

        //startRequest();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (NetworkCallback) context;
        } catch (ClassCastException ignored) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallback = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startRequest() {
        cancelRequest();
        mNetworkTask = new NetworkTask();
        mNetworkTask.execute(url);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing NetworkTask execution.
     */
    public void cancelRequest() {
        if (mNetworkTask != null) {
            mNetworkTask.cancel(true);
            mNetworkTask = null;
        }
    }

    /**
     * Implementation of AsyncTask that runs a network operation on a background thread.
     */
    private class NetworkTask extends AsyncTask<String, Integer, NetworkTask.Result> {
        /**
         * Wrapper class that serves as a union of a result value and an exception. When the
         * network task has completed, either the result value or exception can be a non-null
         * value. This allows you to pass exceptions to the UI thread that were thrown during
         * doInBackground().
         */
        class Result {
            public String mResultValue;
            public Exception mException;
            public Result(String resultValue) {
                mResultValue = resultValue;
            }
            public Result(Exception exception) {
                mException = exception;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mCallback != null) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    mCallback.updateFromRequest(null);
                    cancel(true);
                }
            }
        }

        @Override
        protected Result doInBackground(String... urls) {
            Result result = null;
            if (!isCancelled() && urls != null && urls.length > 0) {
                String strUrl = urls[0];
                try {
                    URL url = new URL(strUrl);
                    String rawResult = open(url);
                    if (rawResult != null) {
                        result = new Result(rawResult);
                    } else {
                        throw new IOException("No response received.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    result = new Result(e);
                }
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values.length >= 2 && mCallback != null) {
                mCallback.onProgressUpdate(values[0], values[1]);
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);

            if (result != null && mCallback != null) {
                if (result.mException != null) {
                    mCallback.updateFromRequest(result.mException.getMessage());
                } else if (result.mResultValue != null) {
                    mCallback.updateFromRequest(result.mResultValue);
                }
                mCallback.finishRequest();
            }
        }

        @Override
        protected void onCancelled(Result result) {
            super.onCancelled(result);

            mCallback.finishRequest();
        }

        /**
         * Given a URL, sets up a connection and gets the HTTP response body from the server.
         * If the network request is successful, it returns the response body in String form. Otherwise,
         * it will throw an IOException.
         */
        private String open(URL url) throws IOException {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.setReadTimeout(3000);
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.setConnectTimeout(3000);
                // For this use case, set HTTP method to GET.
                connection.setRequestMethod("GET");
                // true by default but setting just in case; needs to be true if the request
                // is carrying an input (response) body.
                connection.setDoInput(true);
                // Open communications link (network traffic occurs here).
                connection.connect();

                publishProgress(NetworkCallback.Progress.CONNECT_SUCCESS, 0);

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }

                // Retrieve the response body as an InputStream.
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                publishProgress(NetworkCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);

                String realine;
                StringBuilder stringBuilder = new StringBuilder();
                while ((realine = reader.readLine()) != null) {
                    /*
                     * Since the returned data is in JSON, adding a newline isn't necessary
                     * (It won't affect parsing), but it does make debugging a lot easier
                     * when print out the completed buffer for debugging.
                     */
                    stringBuilder.append(realine).append("\n");
                }
                result = stringBuilder.toString();
                publishProgress(NetworkCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS, 0);
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (reader != null) {
                    reader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }
}
