package sachan.dheeraj.mebeerhu.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by agarwalh on 9/19/2015.
 */
public class DiskLruImageCache {

    private static final String LOG_TAG = DiskLruImageCache.class.getSimpleName();

    private DiskLruCache mDiskCache;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int mCompressQuality = 100;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;

    public DiskLruImageCache( Context context,String uniqueName, int diskCacheSize) {
        try {
            final File diskCacheDir = getDiskCacheDir(context, uniqueName );
            mDiskCache = DiskLruCache.open( diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache.Editor editor )
            throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ), CacheUtils.IO_BUFFER_SIZE );
            return bitmap.compress( mCompressFormat, mCompressQuality, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {

        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !CacheUtils.isExternalStorageRemovable() ?
                        CacheUtils.getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        Log.v(LOG_TAG, "Cache directory : " + (cachePath + File.separator + uniqueName));
        return new File(cachePath + File.separator + uniqueName);
    }

    public void put( String key, Bitmap data ) {

        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit( key );
            if ( editor == null ) {
                return;
            }

            if( writeBitmapToFile( data, editor ) ) {
                mDiskCache.flush();
                editor.commit();
                Log.v(LOG_TAG, "image put on disk cache " + key);
            } else {
                editor.abort();
                Log.v(LOG_TAG, "ERROR on: image put on disk cache " + key);
            }
        } catch (IOException e) {
            Log.v( LOG_TAG, "ERROR on: image put on disk cache " + key );
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }

    public Bitmap getBitmap( String key ) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskCache.get( key );
            if ( snapshot == null ) {
                return null;
            }
            final InputStream in = snapshot.getInputStream( 0 );
            if ( in != null ) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream( in, CacheUtils.IO_BUFFER_SIZE );
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        Log.v(LOG_TAG, bitmap == null ? "" : "image read from disk " + key);
        return bitmap;

    }

    public boolean containsKey( String key ) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get( key );
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        Log.v( LOG_TAG, "disk cache CLEARED");
        try {
            mDiskCache.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

}

