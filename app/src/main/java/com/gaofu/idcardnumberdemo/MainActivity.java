package com.gaofu.idcardnumberdemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("OpenCV");
    }

    private TessBaseAPI                    tessBaseAPI;
    private String                         language = "ck";
    private AsyncTask<Void, Void, Boolean> asyncTask;
    private ProgressDialog                 progressDialog;

    private ImageView idCard;
    private TextView  testtext;
    private int       index = 0;
    private int[]     ids   = {
            R.drawable.id_card0,
            R.drawable.id_card1,
            R.drawable.id_card2,
            R.drawable.id_card3,
            R.drawable.id_card4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idCard = findViewById(R.id.idcard);
        testtext = findViewById(R.id.testtext);
        idCard.setImageResource(R.drawable.id_card0);

        // Example of a call to a native method
        //        TextView tv = findViewById(R.id.sample_text);
        //        tv.setText(stringFromJNI());

        // 初始化 OCR
        tessBaseAPI = new TessBaseAPI();
        initTess();
    }

    @SuppressLint("StaticFieldLeak")
    private void initTess() {
        asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // 目录+文件名 目录下需要 tessdata 目录
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = getAssets().open(language + ".traineddata");
                    File file = new File("/sdcard/tess/tessdata/" + language + ".traineddata");
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        fos = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        return tessBaseAPI.init("/sdcard/tess", language);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != is) {
                            is.close();
                        }
                        if (null != fos) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            protected void onPreExecute() {
                showProgress();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                dismissProgress();
                if (aBoolean) {
                    Toast.makeText(MainActivity.this, "初始化 OCR 成功", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        };
        // 执行
        asyncTask.execute();
    }

    private void showProgress() {
        if (null != progressDialog) {
            progressDialog.show();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("请稍后...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void dismissProgress() {
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //    public native String stringFromJNI();
    public void previous(View view) {
        testtext.setText(null);
        index--;
        if (index < 0) {
            index = ids.length - 1;
        }
        idCard.setImageResource(ids[index]);
    }

    public void next(View view) {
        testtext.setText(null);
        index++;
        if (index >= ids.length) {
            index = 0;
        }
        idCard.setImageResource(ids[index]);
    }

    public void rt(View view) {
        // 获取身份证图片的号码区域
        // 获取原图
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), ids[index]);
        Bitmap idNumber = findIdNumber(bitmap, Bitmap.Config.ARGB_8888);
        bitmap.recycle();
        //
        if (null != idNumber) {
            idCard.setImageBitmap(idNumber);
        } else {
            return;
        }
        // OCR 文字识别
        tessBaseAPI.setImage(idNumber);
        testtext.setText(tessBaseAPI.getUTF8Text());
    }

    private native Bitmap findIdNumber(Bitmap bitmap, Bitmap.Config argb8888);

}