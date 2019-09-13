package com.xinye.image.compress;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.xinye.image.compress.permission.PermissionManager;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAllPermission();



        statusTextView = findViewById(R.id.tvStatus);
        Button button = findViewById(R.id.btnExecute);
        final EditText inputEditText = findViewById(R.id.etInputPath);
        final EditText outputEditText = findViewById(R.id.etOutputPath);
        final CheckBox checkBox = findViewById(R.id.cbDeleteSource);

        outputEditText.setText("/mnt/sdcard/DCIM/Camera-" + getDateString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPath = inputEditText.getText().toString().trim();
                String outputPath = outputEditText.getText().toString().trim();
                if(inputPath.length() <= 0){
                    statusTextView.setText("请输入输入目录地址");
                    return;
                }

                File file = new File(inputPath);
                if(!file.exists()){
                    statusTextView.setText("输入目录地址不存在");
                    return;
                }

                if(!file.isDirectory()){
                    statusTextView.setText("输入地址不是一个目录");
                    return;
                }

                if(outputPath.length() <= 0){
                    statusTextView.setText("请输入输出目录地址");
                    return;
                }

                final File[] list = file.listFiles();
                if(list == null || list.length <= 0){
                    statusTextView.setText("输入目录中没有文件");
                    return;
                }

                final File outDir = new File(outputPath);
                if(!outDir.exists()){
                    statusTextView.setText("创建输出目录失败");
                    outDir.mkdirs();
                }

                if(!outDir.exists()){
                    return;
                }

                compress(list,0,outDir,checkBox.isChecked());

            }
        });
    }

    private void compress(final File[] list, final int index, final File outDir, final boolean delete){
        if(list == null || list.length == 0 || index < 0 || index > list.length - 1){
            return;
        }
        final File inputFile = list[index];
        final File outFile = new File(outDir, inputFile.getName());

        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        options.quality = 90;
        options.outfile = outFile.getAbsolutePath();

        TextView tv = statusTextView;
        Tiny.getInstance().source(inputFile.getAbsoluteFile()).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(final boolean isSuccess, String outfile, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = statusTextView;
                        if(tv != null){
                            if(index == list.length - 1){
                                tv.setText("全部文件压缩完成(" + (index + 1) + "/" + list.length + ")");
                            }else{
                                tv.setText("目标文件(" + (index + 1) + "/" + list.length + "):" + inputFile.getAbsolutePath() + "压缩成功:" + isSuccess);
                            }
                        }

                    }
                });
                compress(list,index + 1,outDir,delete);
                if(isSuccess && delete){
                    inputFile.delete();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestAllPermission();
    }

    private void requestAllPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        PermissionManager.INSTANCE.checkAllPermission(this, permissions, new Function2<ArrayList<String>, Boolean, Unit>() {
            @Override
            public Unit invoke(ArrayList<String> deniedPermissions, Boolean isAll) {
                if(!isAll){
                    PermissionManager.INSTANCE.requestPermission(MainActivity.this, deniedPermissions, new Function2<Boolean, Boolean, Unit>() {
                        @Override
                        public Unit invoke(Boolean aBoolean, Boolean aBoolean2) {
                            return null;
                        }
                    });
                }
                return null;
            }
        });
    }

    private String getDateString(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String monthString = String.valueOf(month);
        if(month < 10){
            monthString = "0" + month;
        }

        String dayString = String.valueOf(day);
        if(day < 10){
            dayString = "0" + dayString;
        }

        return year + monthString + dayString;
    }

}
