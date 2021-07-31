package com.b18060412.superdiary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.fragment.app.Fragment;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class FileManager {
    private static final String TAG = "FileManager";


    private File fileDir;
    private Context mContext;

    public final static int ROOT_DIR = 0;
    public final static int TEMP_DIR = 1;

    private final static String TEMP_DIR_STR = "temp/";

    public FileManager(Context context, int dir) {
        this.mContext = context;
        switch (dir) {
            case ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir("");
                break;
            case TEMP_DIR:
                this.fileDir = mContext.getExternalFilesDir(TEMP_DIR_STR);
                break;
        }
    }

    public static String createRandomFileName() {
        return UUID.randomUUID().toString();
    }

    public void clearDir() {
        File[] fList = fileDir.listFiles();
        if (fList != null && fileDir.isDirectory()) {
            try {
                FileUtils.cleanDirectory(fileDir);
            } catch (IOException e) {
                Log.e(TAG, "ClearDir file", e);
            }
        }
    }
    public File getDir() {
        return fileDir;
    }

    public static String getFileNameByUri(Context context, Uri uri) {
        String displayName = "";
        if (uri.getScheme().toString().startsWith("content")) {
            Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        } else if (uri.getScheme().toString().startsWith("file")) {
            try {
                File file = new File(new URI(uri.toString()));
                if (file.exists()) {
                    displayName = file.getName();
                }
            } catch (URISyntaxException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
        } else {
            File file = new File(uri.getPath());
            if (file.exists()) {
                displayName = file.getName();
            }
        }
        return displayName;
    }


}
