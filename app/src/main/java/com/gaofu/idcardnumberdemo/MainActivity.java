package com.gaofu.idcardnumberdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("OpenCV");
    }

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
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

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

    }

    private native Bitmap findIdNumber(Bitmap bitmap, Bitmap.Config argb8888);

}
