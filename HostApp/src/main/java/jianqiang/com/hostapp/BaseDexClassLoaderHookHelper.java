package jianqiang.com.hostapp;

import com.example.jianqiang.mypluginlibrary.RefInvoke;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * 由于应用程序使用的ClassLoader为PathClassLoader
 * 最终继承自 BaseDexClassLoader
 * 查看源码得知,这个BaseDexClassLoader加载代码根据一个叫做
 * dexElements的数组进行, 因此我们把包含代码的dex文件插入这个数组
 * 系统的classLoader就能帮助我们找到这个类
 *
 * 这个类用来进行对于BaseDexClassLoader的Hook
 * 类名太长, 不要吐槽.
 * @author weishu
 * @date 16/3/28
 */
public final class BaseDexClassLoaderHookHelper {

    public static void patchClassLoader(ClassLoader cl, File optDexFile) throws  IOException {
        // 获取 BaseDexClassLoader : pathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(), cl, "pathList");

        // 获取 PathList: Element[] dexElements
        Object[] dexElements = (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");

        // Element 类型
        Class<?> elementClass = dexElements.getClass().getComponentType();

        // 创建一个数组, 用来替换原始的数组
        Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);

        DexFile dexFile = new DexFile(optDexFile);
        //Constructor[] constructors = elementClass.getConstructors();
        Class[] p1 = {DexFile.class, File.class};
        Object[] v1 = {dexFile, optDexFile};

        Object o = RefInvoke.createObject(elementClass, p1, v1);

        Object[] toAddElementArray = new Object[] { o };
        // 把原始的elements复制进去
        System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
        // 插件的那个element复制进去
        System.arraycopy(toAddElementArray, 0, newElements, dexElements.length, toAddElementArray.length);

        // 替换
        RefInvoke.setFieldObject(pathListObj, "dexElements", newElements);
    }

    public static void patchClassLoader(ClassLoader cl, String dexPath) throws InvocationTargetException, IllegalAccessException {
        // 获取 BaseDexClassLoader : pathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(), cl, "pathList");

        // 获取 PathList: Element[] dexElements
        Object[] oldDexElements = (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");
        int oldLength = oldDexElements.length;

        //2， 调用 pathList 中的 addDexPath
        Method method = RefInvoke.getMethod(cl, "addDexPath", String.class);
        if (method != null) {
            method.invoke(cl, dexPath);
        }

        Object[] newDexElements = (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");
        int newLength = newDexElements.length;

        // Element 类型
        Class<?> elementClass = oldDexElements.getClass().getComponentType();
        // 创建一个数组, 用来替换原始的数组
        Object[] resultElements = (Object[]) Array.newInstance(elementClass, newLength);


        //把oldDexElement 复制到数组尾部
        System.arraycopy(newDexElements, 0, resultElements, newLength - oldLength, oldLength);
        //把刚加入的element 复制到数组前面
        System.arraycopy(newDexElements, oldLength, resultElements, 0, newLength - oldLength);

        //替换
        RefInvoke.setFieldObject(pathListObj, "dexElements", resultElements);
    }
}
