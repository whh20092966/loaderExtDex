package jianqiang.com.hostapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.File;

import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {
    PluginItem pluginItem1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(newBase, "plugin1.apk");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pluginItem1 = generatePluginItem("plugin1.apk");
    }

    public void startService1InPlugin1(View view) {
        try {
            Intent intent = new Intent();
            String serviceName = pluginItem1.packageInfo.packageName + ".TestService1";
            intent.setClass(this, Class.forName(serviceName));
            startService(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopService1InPlugin1(View view) {
        //tbd
    }

    private PluginItem generatePluginItem(String apkName) {
        File file = getFileStreamPath(apkName);
        PluginItem item = new PluginItem();
        item.pluginPath = file.getAbsolutePath();
        item.packageInfo = DLUtils.getPackageInfo(this, item.pluginPath);

        return item;
    }
}