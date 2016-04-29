package net.println.easydroid.file;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by benny on 4/29/16.
 */
public class EFile {
    public static final String TAG = "EFile";

    private File file;

    private String text;

    private JSONArray jsonArray;

    private JSONObject jsonObject;

    public EFile(String path){
        file = new File(path);
    }

    public EFile(String dirPath, String name){
        file = new File(dirPath, name);
    }

    public EFile(File dir, String name) {
        this(dir == null ? null : dir.getPath(), name);
    }

    /**
     * >benny: [16-04-29 08:58] Easy method to get file content. Be careful when the file is large.
     * @return
     */
    public String text(){
        if(file.length() > Integer.MAX_VALUE) return null;
        if(TextUtils.isEmpty(text)){
            try{
                ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while((len = fis.read(bytes)) != -1){
                    bos.write(bytes, 0 , len);
                }
                fis.close();
                text  = bos.toString();
                bos.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return text;
    }

    public JSONObject jsonObject(){
        if(jsonObject == null){
            try {
                jsonObject = new JSONObject(text());
            } catch (JSONException e) {
                e.printStackTrace();
                jsonObject = new JSONObject();
            }
        }
        return jsonObject;
    }

    public JSONArray jsonArray(){
        if(jsonArray == null){
            try {
                jsonArray = new JSONArray(text());
            } catch (JSONException e) {
                e.printStackTrace();
                jsonArray = new JSONArray();
            }
        }
        return jsonArray;
    }

    public boolean copyInto(String destDirPath, boolean replace){
        return copyInto(new File(destDirPath), replace);
    }

    public boolean copyInto(File destDir, boolean replace){
        File destFile = new File(destDir, this.file.getName());
        return copyTo(destFile, replace);
    }

    public boolean copyTo(String path, boolean replace){
        return copyTo(new File(path), replace);
    }

    /**
     * If this file is a dir, target path should also be a dir. At least I will treat it as a dir.
     * @param destFile
     * @param replace
     * @return
     */
    public boolean copyTo(File destFile, boolean replace){
        System.out.println(this.file.getPath()+"->" + destFile.getAbsolutePath() + ", " + replace);
        if(file.isDirectory()){
            if(!destFile.exists()) {
                if(!destFile.mkdirs()){
                    return false;
                }
            }

            String[] subFilePaths = file.list();
            boolean result = true;
            for(String subFilePath : subFilePaths){
                EFile eFile = new EFile(this.file,subFilePath);
                result &= eFile.copyTo(destFile.getPath() + File.separator + subFilePath, replace);
            }
            return result;
        }else {
            //File destFile = new File(path);
            if(destFile.isDirectory()){
                if(!destFile.exists()){
                    if(!destFile.mkdirs()){
                        return false;
                    }
                }
                destFile = new File(destFile, this.file.getName());
            }
            if(destFile.exists()){
                if(replace) {
                    destFile.delete();
                }else{
                    return true;
                }
            }
            // copy.
            try {
                FileOutputStream fos = new FileOutputStream(destFile);
                FileInputStream fis = new FileInputStream(this.file);
                byte[] buffer = new byte[1024];
                int len;
                while((len = fis.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                }
                fis.close();
                fos.close();
                System.out.println("succeed.");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("failed.");
                return false;
            }
        }
        return true;
    }

    public boolean moveTo(String path){
        return moveTo(new File(path));
    }

    public boolean moveTo(File file){
        return this.file.renameTo(file);
    }

    public boolean moveInto(String path){
        return moveTo(new File(path, this.file.getName()));
    }

    public boolean moveInto(File destDir){
        return moveTo(new File(destDir, this.file.getName()));
    }

}
