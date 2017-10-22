package my.com.codeplay.training.network_demo;

import android.net.NetworkInfo;

/**
 * An interface containing methods needed for an asynchronous network task
 * to update the UI Context.
 */
public interface NetworkCallback {
    interface Progress {
        int ERROR = -1;
        int CONNECT_SUCCESS = 0;
        int GET_INPUT_STREAM_SUCCESS = 1;
        int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
        int PROCESS_INPUT_STREAM_SUCCESS = 3;
    }

    /**
     * Get the device's active network status in the form of a NetworkInfo object.
     */
    NetworkInfo getActiveNetworkInfo();

    void startRequest();

    /**
     * Indicate to callback handler any progress update.
     * @param progressCode must be one of the constants defined in NetworkCallback.Progress.
     * @param percentComplete must be 0-100.
     */
    void onProgressUpdate(int progressCode, int percentComplete);

    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void updateFromRequest(String result);

    /**
     * Indicates that the network operation has finished. This method is called even if the
     * network request hasn't completed successfully.
     */
    void finishRequest();
}
