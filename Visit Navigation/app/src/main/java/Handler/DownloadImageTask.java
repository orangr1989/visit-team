package Handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

/**
 * Created by nisan on 16/05/2016.
 */
public class DownloadImageTask extends AsyncTask<String, Void, String> {
    private static final String TAG = DownloadImageTask.class.getSimpleName();
    private static final String FILE_EXT = ".jpg";

    private Context context;
    private DownloadImageTaskCallback callback;
    private String url;

    public DownloadImageTask(Context newContext, DownloadImageTaskCallback callback) {
        this.callback = callback;
        context = newContext;
    }
    @Override
    protected String doInBackground(String... params) {
        Bitmap bm = null;

        if (params.length == 0)
            return null;

        String urlStr = params[0];
        url = urlStr;
        String md5 = "";

        try {
            md5 = md5(urlStr);
            File f = context.getFileStreamPath(md5 + FILE_EXT);

            if (f.exists()) {
                return f.getAbsolutePath();
            }

        } catch (FileNotFoundException fnf) {
            String n = "test";
        } catch (Exception e1) {
            String n = "test";
        }

        InputStream is = null;
        try {

            if (urlStr != null && url.indexOf("http://{HOST}:{PORT}") != -1) {
                urlStr = urlStr.replace("{HOST}", ConnectionHandler.host);
                urlStr = urlStr.replace("{PORT}", ConnectionHandler.port + "");
            }
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.connect();

            is = conn.getInputStream();
            bm = BitmapFactory.decodeStream(is);


        } catch (IOException e) {
            Log.i(TAG, "Download of image failed: " + e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

        if (bm != null) {
            FileOutputStream fos = null;
            try {

                fos = context.openFileOutput(md5 + FILE_EXT,
                        Context.MODE_WORLD_READABLE);
                bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);

            } catch (Exception e) {
                Log.e(TAG, "Storage of image failed: " + e.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                    }
                }

                if (bm != null) {
                    bm.recycle();
                }
            }
        }

        File f = context.getFileStreamPath(md5 + FILE_EXT);

        if (f.exists()) {
            return f.getAbsolutePath();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback != null) {
            try {
                if (result != null) {
                    callback.onImageDownloaded(url, result);
                } else {
                    callback.onImageDownloadFailure(url);
                }
            } catch (WindowManager.BadTokenException e) {
                Log.w(TAG, "Callback failed, caught BadTookenException: " + e.getMessage(), e);
            } catch (Exception e) {
                Log.w(TAG, "Callback failed, caught Exception: " + e.getMessage(), e);
            }
            callback = null;
            url = null;
            context = null;
        }
    }

    /**
     *
     * @param s
     *            URL of image
     * @return md5 hash value of image URL
     * @throws Exception
     */
    static protected String md5(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(s.getBytes());

        byte digest[] = md.digest();
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < digest.length; i++) {
            result.append(Integer.toHexString(0xFF & digest[i]));
        }

        return result.toString();
    }

    public interface DownloadImageTaskCallback {
        /**
         *
         * @param url
         *            URL of the image that was downloaded
         * @param path
         *            Absolute path of the downloaded and cached image
         */
        public void onImageDownloaded(String url, String path);
        public void onImageDownloadFailure(String url);
    }
}
