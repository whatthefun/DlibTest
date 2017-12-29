package com.example.yuan.myapplication;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.PeopleDet;
import com.tzutalin.dlib.VisionDetRet;
import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final String[] STORAGE =
        { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private static final int STORAGE_PERM = 123;
    ImageView image;
    Paint paint;
    Canvas canvas;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate start.");
        storagePermission();
        Log.d(TAG, "onCreate: " + Environment.getExternalStorageState());

        image = findViewById(R.id.imageView);
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        //Log.d(TAG, "w: " + image.getWidth());

        PeopleDet peopleDet = new PeopleDet();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.messi);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();

        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(newb);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(bmp, 0, 0, paint);
        image.setImageBitmap(newb);

        List<VisionDetRet> results =
            peopleDet.detBitmapFace(bmp, Constants.getFaceShapeModelPath());
        Log.d(TAG, "onCreate: " + results);
        for (final VisionDetRet ret : results) {
            Log.d(TAG, "onCreate: DetResult");
            String label = ret.getLabel(); // If doing face detection, it will be 'Face'
            int rectLeft = ret.getLeft();
            int rectTop = ret.getTop();
            int rectRight = ret.getRight();
            int rectBottom = ret.getBottom();
            ArrayList<Point> landmarks = ret.getFaceLandmarks();

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);

            Path path = new Path();
            path.setFillType(Path.FillType.INVERSE_EVEN_ODD);

            //下臉的輪廓
            for (int i = 0; i <= 16; i++){
                int pointX = landmarks.get(i).x;
                int pointY = landmarks.get(i).y;
                path.lineTo(pointX, pointY);
            }

            //上臉的輪廓
            int pointX, pointY;
            int length = landmarks.get(27).x - landmarks.get(23).x;
            //右上角
            pointX = landmarks.get(16).x;
            pointY = landmarks.get(25).y + length;

            path.lineTo(pointX, pointY);
            //左上角
            pointX = landmarks.get(1).x;
            pointY = landmarks.get(25).y + length;
            path.lineTo(pointX, pointY);
            //回到原點
            path.lineTo(landmarks.get(0).x, landmarks.get(0).y);


            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawPath(path, paint);
            canvas.drawBitmap(newb, 0, 0, paint);

            Bitmap croppedBitmap = Bitmap.createBitmap(newb, rectLeft-50, rectTop-50, rectRight - rectLeft+50, rectBottom - rectTop+50);
            canvas.drawBitmap(croppedBitmap, 0, 0, paint);
        }
    }

    @AfterPermissionGranted(STORAGE_PERM) public void storagePermission() {
        if (EasyPermissions.hasPermissions(this, STORAGE)) {
            Toast.makeText(this, "STORAGE_PERMISSION", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "", STORAGE_PERM, STORAGE);
        }
    }

    @Override public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
