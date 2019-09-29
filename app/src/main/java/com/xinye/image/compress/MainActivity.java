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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_EXT_POST = ".123";

    private TextView mStatusTextView;
    private EditText mCompressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAllPermission();

        mStatusTextView = findViewById(R.id.tvStatus);

        initCompressComponent();

        initReplyComponent();

        initZipComponent();

    }

    private void initZipComponent() {
        final EditText zipEditText = findViewById(R.id.etZipPath);
        Button btnZipSetDirName = findViewById(R.id.btnZipSetToOutputDir);
        Button btnZip = findViewById(R.id.btnZip);

        zipEditText.setText(mCompressEditText.getText());

        btnZipSetDirName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zipEditText.setText(mCompressEditText.getText());
            }
        });

        btnZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = zipEditText.getText().toString().trim();
                if(path.length() <= 0){
                    mStatusTextView.setText("请输入待压缩目录地址");
                    return;
                }
                File file = new File(path);
                if(!file.exists()){
                    mStatusTextView.setText("文件路径不存在");
                    return;
                }

                if(!file.isDirectory()){
                    mStatusTextView.setText("地址不是一个目录");
                    return;
                }


                final File[] list = file.listFiles();
                if(list == null || list.length <= 0){
                    mStatusTextView.setText("目录中没有文件");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File outFile = new File(path,getDateString() + "-" + System.currentTimeMillis() + ".zip");
                        ZipOutputStream os = null;
                        try {
                            os = new ZipOutputStream(new FileOutputStream(outFile));
                            for(File f : list){
                                String name = f.getName();
                                os.putNextEntry(new ZipEntry(name));

                                FileInputStream input = new FileInputStream(f);
                                byte[] buf = new byte[1024];
                                int len = -1;

                                while ((len = input.read(buf)) != -1) {
                                    os.write(buf, 0, len);
                                }
                                f.delete();
                                final String filePath = f.getAbsolutePath();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mStatusTextView.setText("写入文件完成:" + filePath);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally{
                            try {
                                if(os != null) {
                                    os.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStatusTextView.setText("压缩到Zip文件成功:" + outFile.getAbsolutePath());
                            }
                        });
                    }
                }).start();

            }
        });
    }

    private void initReplyComponent() {
        final EditText outputReplyEditText = findViewById(R.id.etOutputReplyPath);
        outputReplyEditText.setText(mCompressEditText.getText());

        Button btnReplySetDirName = findViewById(R.id.btnReplySetToOutputDir);
        Button btnReply = findViewById(R.id.btnReply);
        btnReplySetDirName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputReplyEditText.setText(mCompressEditText.getText());
            }
        });

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = outputReplyEditText.getText().toString().trim();
                if(path.length() <= 0){
                    mStatusTextView.setText("请输入目录地址");
                    return;
                }
                File file = new File(path);
                if(!file.exists()){
                    mStatusTextView.setText("文件路径不存在");
                    return;
                }

                if(!file.isDirectory()){
                    mStatusTextView.setText("地址不是一个目录");
                    return;
                }


                final File[] list = file.listFiles();
                if(list == null || list.length <= 0){
                    mStatusTextView.setText("目录中没有文件");
                    return;
                }

                renameFileList(list,file);

            }
        });
    }

    private void initCompressComponent() {
        final EditText inputEditText = findViewById(R.id.etInputPath);
        mCompressEditText = findViewById(R.id.etOutputPath);
        mCompressEditText.setText("/mnt/sdcard/DCIM/Camera-" + getDateString());

        final CheckBox deleteCheckBox = findViewById(R.id.cbDeleteSource);
        final CheckBox renameCheckBox = findViewById(R.id.cbRenameFile);
        Button btnExecuteCompress = findViewById(R.id.btnExecute);
        btnExecuteCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPath = inputEditText.getText().toString().trim();
                String outputPath = mCompressEditText.getText().toString().trim();
                if(inputPath.length() <= 0){
                    mStatusTextView.setText("请输入输入目录地址");
                    return;
                }

                File file = new File(inputPath);
                if(!file.exists()){
                    mStatusTextView.setText("输入目录地址不存在");
                    return;
                }

                if(!file.isDirectory()){
                    mStatusTextView.setText("输入地址不是一个目录");
                    return;
                }

                if(outputPath.length() <= 0){
                    mStatusTextView.setText("请输入输出目录地址");
                    return;
                }

                final File[] list = file.listFiles();
                if(list == null || list.length <= 0){
                    mStatusTextView.setText("输入目录中没有文件");
                    return;
                }

                final File outDir = new File(outputPath);
                if(!outDir.exists()){
                    mStatusTextView.setText("创建输出目录失败");
                    outDir.mkdirs();
                }

                if(!outDir.exists()){
                    return;
                }

                compress(list,0,outDir,deleteCheckBox.isChecked(),renameCheckBox.isChecked());

            }
        });
    }

    private void renameFileList(final File[] list,final File dir) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dirPath = dir.getAbsolutePath();
                int count = list.length;
                int index = 0;
                for(File f : list){
                    String name = f.getName();
                    if(name.contains(FILE_EXT_POST)){
                        name = name.replaceAll(FILE_EXT_POST,"");
                        f.renameTo(new File(dirPath,name));
                    }
                    index++;
                    final String text = "当前第" + index + "个文件重命名完成，共" + count + "个";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStatusTextView.setText(text);
                        }
                    });
                }
            }
        }).start();
    }

    private void compress(final File[] list, final int index, final File outDir, final boolean delete,final boolean rename){
        if(list == null || list.length == 0 || index < 0 || index > list.length - 1){
            return;
        }
        final File inputFile = list[index];
        final String fileName = inputFile.getName();

        final File outFile = new File(outDir, fileName);

        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        options.quality = 95;
        options.outfile = outFile.getAbsolutePath();

        TextView tv = mStatusTextView;
        Tiny.getInstance().source(inputFile.getAbsoluteFile()).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(final boolean isSuccess, String outfile, Throwable t) {
                if(rename){
                    String newFileName = fileName + FILE_EXT_POST;
                    outFile.renameTo(new File(outDir,newFileName));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = mStatusTextView;
                        if(tv != null){
                            if(index == list.length - 1){
                                tv.setText("全部文件压缩完成(" + (index + 1) + "/" + list.length + ")");
                            }else{
                                tv.setText("目标文件(" + (index + 1) + "/" + list.length + "):" + inputFile.getAbsolutePath() + "压缩成功:" + isSuccess);
                            }
                        }

                    }
                });
                compress(list,index + 1,outDir,delete,rename);
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
