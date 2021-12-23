package jianqiang.com.hostapp;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MyApplication extends Application {
    private static final String apkName = "plugin1.apk";
    private static final String dexName = "plugin1.dex";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        Utils.extractAssets(newBase, apkName);
        
        File apkFile = new File("/data/data/jianqiang.com.hostapp/files/plugin1.apk");     // getFileStreamPath(apkName);
        File optDexFile = new File("/data/data/jianqiang.com.hostapp/files/classes.dex");  //getFileStreamPath(dexName);

        try {
            //BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), "/data/data/jianqiang.com.hostapp/files/classes.dex");
            //BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), "/data/data/jianqiang.com.hostapp/files/plugin1.apk");

            //BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), optDexFile);
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), apkFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
