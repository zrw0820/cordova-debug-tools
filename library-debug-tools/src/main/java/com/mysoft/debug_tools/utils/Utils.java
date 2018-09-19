package com.mysoft.debug_tools.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zourw on 2018/9/14.
 */
public class Utils {
    public static final DateFormat DEFAULT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.ENGLISH);
    public static final DateFormat WITHOUT_MILLIS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    public static final DateFormat HHMMSS = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    public static String millis2String(final long millis) {
        return millis2String(millis, DEFAULT);
    }

    public static String millis2String(final long millis, DateFormat format) {
        return format.format(new Date(millis));
    }

    public static <V> int getCount(V[] sourceList) {
        return (sourceList == null || sourceList.length == 0) ? 0 : sourceList.length;
    }

    public static List<File> getFiles(File file) {
        List<File> descriptors = new ArrayList<>();
        if (file.isDirectory() && file.exists()) {
            descriptors.addAll(Arrays.asList(file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.getName().startsWith(".");
                }
            })));
        }
        return descriptors;
    }

    public static void writeTextToFile(File file, String content) {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write(content, 0, content.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readTextFile(File textFile) {
        StringBuilder builder = new StringBuilder();

        InputStreamReader is = null;
        BufferedReader br = null;
        try {
            is = new InputStreamReader(new FileInputStream(textFile));
            br = new BufferedReader(is);

            String buffer;
            while (!TextUtils.isEmpty(buffer = br.readLine())) {
                builder.append(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    @NonNull
    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    @NonNull
    public static List<Pair<String, String>> parser(Signature signature) {
        List<Pair<String, String>> pairs = new ArrayList<>();
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
            byte[] encoded = cert.getEncoded();

            pairs.add(Pair.create("MD5：", byte2HexFormatted(MessageDigest.getInstance("MD5").digest(encoded))));
            pairs.add(Pair.create("SHA1：", byte2HexFormatted(MessageDigest.getInstance("SHA1").digest(encoded))));
            pairs.add(Pair.create("SHA256：", byte2HexFormatted(MessageDigest.getInstance("SHA256").digest(encoded))));
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return pairs;
    }

    public static boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        }
        return true;
    }

    private static long lastCheckTime;
    private static float[] lastXyz = new float[3];

    public static boolean checkIfShake(float x, float y, float z) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - lastCheckTime;
        if (diffTime < 100) {
            return false;
        }
        lastCheckTime = currentTime;
        float deltaX = x - lastXyz[0];
        float deltaY = y - lastXyz[1];
        float deltaZ = z - lastXyz[2];
        lastXyz[0] = x;
        lastXyz[1] = y;
        lastXyz[2] = z;
        int delta = (int) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000);
        return delta > 1000;
    }

    public static void copyText(Context context, CharSequence label, CharSequence text) {
        try {
            ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (manager != null) {
                manager.setPrimaryClip(ClipData.newPlainText(label, text));
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}