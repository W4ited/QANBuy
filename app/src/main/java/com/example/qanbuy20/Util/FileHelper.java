package com.example.qanbuy20.Util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//context方式的文件存储
public class FileHelper {
    private Context context;

    public FileHelper() {
    }

    public FileHelper(Context context) {
        this.context = context;
    }

    //context 保存方式
    /*
     *	 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
     *	*/
    public void save(String fileName, String fileContext) throws Exception {
        //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
        FileOutputStream output = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        output.write(fileContext.getBytes());         //将 String 字符串以字节流的形式写入到输出流中
        output.close();                               //关闭输出流
    }

    /*
     *	 这里定义的是文件读取的方法
     *	*/
    public String read(String filename) throws IOException {
        //打开文件输入流
        FileInputStream input = context.openFileInput(filename);
        //每次读取1024个byte的数据
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        //读取文件内容:
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        //关闭输入流
        input.close();
        return sb.toString();
    }

    //判断文件是否存在
    public boolean fileIsExist(String filename) {
        try {
            File file = new File(context.getFilesDir(), filename);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //删除文件
    public void fileDelete(String filename) {
        try {
            File file = new File(context.getFilesDir(), filename);
            if (file.exists()) {
                context.deleteFile(filename);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
