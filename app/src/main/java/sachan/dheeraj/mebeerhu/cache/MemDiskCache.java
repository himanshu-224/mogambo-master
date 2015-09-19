package sachan.dheeraj.mebeerhu.cache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by agarwalh on 9/19/2015.
 */
public class MemDiskCache {

    private static final String LOG_TAG = MemDiskCache.class.getSimpleName();
    private static MemDiskCache mMemDiskCache = null;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruImageCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "cached_images";
    private static Context context;

    /* Make it a singleton class */
    private MemDiskCache()
    {

    }

    public static void createInstance(Activity activity)
    {
        if (mMemDiskCache == null)
        {
            context = (Context)activity;
            mMemDiskCache = new MemDiskCache();
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            Log.v (LOG_TAG, "Memory cache size = " + cacheSize);

            mMemDiskCache.mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };

            // Initialize disk cache on background thread
            new AsyncTask<File, Void, Void> (){
                @Override
                protected Void doInBackground(File... params) {
                    synchronized (mMemDiskCache.mDiskCacheLock) {
                        mMemDiskCache.mDiskLruCache = new DiskLruImageCache(context, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE );
                        mMemDiskCache.mDiskCacheStarting = false; // Finished initialization
                        mMemDiskCache.mDiskCacheLock.notifyAll(); // Wake any waiting threads
                    }
                    return null;
                }
            }.execute();

        }
    }

    public static MemDiskCache getInstance()
    {
        if (mMemDiskCache == null)
        {
            Log.e(LOG_TAG, "Mem Disk cache is not created somehow");
            return null;
        }
        else
            return mMemDiskCache;
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        // Add to memory cache as before
        if (getBitmapFromMemCache(key) == null) {
            Log.v(LOG_TAG, String.format("Putting key %s in Memory cache", key));
            mMemoryCache.put(key, bitmap);
        }
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        // Add to memory cache as before
        if (getBitmapFromMemCache(key) == null) {
            Log.v(LOG_TAG, String.format("Putting key %s in Memory cache", key));
            mMemoryCache.put(key, bitmap);
        }

        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && !mDiskLruCache.containsKey(key)) {
                Log.v(LOG_TAG, String.format("Putting key %s in Disk cache", key));
                mDiskLruCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromCache(String key)
    {
        Log.v(LOG_TAG, String.format("Fetching key %s from Memory cache", key));
        Bitmap bitmap = getBitmapFromMemCache(key);
        if ( bitmap == null) {
            Log.v(LOG_TAG, String.format("Fetching key %s from Disk cache", key));
            bitmap = getBitmapFromDiskCache(key);
            if(bitmap != null){
                addBitmapToMemCache(key, bitmap);
            }
        }
        if (bitmap == null) {
            Log.v(LOG_TAG, String.format("Couldn't find key %s in any cache", key));
        }
        return bitmap;
    }

    public Bitmap getBitmapFromMemCache(String key) {
        if (mMemDiskCache != null)
            return mMemoryCache.get(key);
        else
            return null;
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }
            if (mDiskLruCache != null) {
                    return mDiskLruCache.getBitmap(key);
            }
        }
        return null;
    }

}
