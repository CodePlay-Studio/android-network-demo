package my.com.codeplay.training.network_demo.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyManager {
    private static final int SOCKET_TIMEOUT_MS = 30 * 1000;
    private static final int IMAGE_CACHE_ENTYR = 10;

    private static volatile VolleyManager instance;
    private final RequestQueue requestQueue;
    private final ImageLoader imageLoader;

    public static synchronized VolleyManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new VolleyManager(ctx.getApplicationContext());
        }
        return instance;
    }

    private VolleyManager(Context ctx) {
        requestQueue = Volley.newRequestQueue(ctx);

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> imageCached
                    = new LruCache<>(IMAGE_CACHE_ENTYR);
            @Override
            public Bitmap getBitmap(String url) {
                return imageCached.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                imageCached.put(url, bitmap);
            }
        });

        // imageLoader = new ImageLoader(requestQueue, new LruBitmapCache(ctx));
    }

    public <T> void enqueue(Request<T> request, String tag) {
        if (!TextUtils.isEmpty(tag))
            request.setTag(tag);
        enqueue(request);
    }

    public <T> void enqueue(Request<T> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS, // default is 2500ms
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    public void cancelRequest(String tag) {
        if (!TextUtils.isEmpty(tag))
            requestQueue.cancelAll(tag);
    }

    public void loadImage(String url, ImageView iv, int placeholderDrawable, int errorDrawable) {
        imageLoader.get(url, ImageLoader.getImageListener(iv, placeholderDrawable, errorDrawable));
    }
}
