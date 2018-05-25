package com.lilong.hotfixdemo.hotfix;

import android.content.res.AssetManager;

import com.lilong.hotfixdemo.application.DemoApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by lilong on 18-5-25.
 */

public class HotfixManager {

    private static HotfixManager sInstance;

    private HotfixManager() {

    }

    public static HotfixManager getInstance() {
        if (sInstance == null) {
            synchronized (HotfixManager.class) {
                if (sInstance == null) {
                    sInstance = new HotfixManager();
                }
            }
        }
        return sInstance;
    }

    public static final String PATCH_DEX_NAME = "fixed.dex";

    /**
     * 补丁dex要被复制到的目录绝对路径
     */
    public String getPatchDexDestDirAbsPath() {
        return DemoApplication.getInstance().getFilesDir().getAbsolutePath();
    }

    /**
     * 补丁dex要被复制到的位置绝对路径
     */
    public String getPatchDexDestAbsPath() {
        return getPatchDexDestDirAbsPath() + File.separator + PATCH_DEX_NAME;
    }

    /**
     * 补丁dex被dexClassLoader加载后的optimized directory
     */
    public String getPatchDexOptDirAbsPath() {
        return getPatchDexDestDirAbsPath() + File.separator + "optimizedDir";
    }

    public void init() {
        copyPatchDexFromAssetsToFileDir();
        ensurePathDexOptDir();
    }

    /**
     * 将补丁dex从assets目录拷贝到file目录
     */
    // TODO 放到工作线程中
    private void copyPatchDexFromAssetsToFileDir() {

        // 如果该位置已经有了，就不拷贝了
//        if (new File(getPluginApkDestAbsPath()).exists()) {
//            return;
//        }

        AssetManager assetManager = DemoApplication.getInstance().getAssets();
        InputStream in;
        BufferedInputStream bin;
        FileOutputStream fout;
        try {
            in = assetManager.open(PATCH_DEX_NAME);
            bin = new BufferedInputStream(in);
            fout = new FileOutputStream(getPatchDexDestAbsPath());
            byte[] data = new byte[1024];
            int length = 0;
            while ((length = bin.read(data)) != -1) {
                fout.write(data, 0, length);
            }
            bin.close();
            fout.flush();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bin = null;
            fout = null;
        }
    }

    /**
     * 确保已经创建补丁dex被加载后的optimized dir
     */
    public void ensurePathDexOptDir() {
        File optDir = new File(getPatchDexOptDirAbsPath());
        if (optDir.exists() && optDir.isDirectory()) {
            return;
        }
        optDir.mkdir();
    }

    public void hotfix() {

        PathClassLoader appClassLoader = (PathClassLoader) DemoApplication.getInstance().getClassLoader();
        DexClassLoader patchClassLoader = new DexClassLoader(getPatchDexDestAbsPath(), getPatchDexOptDirAbsPath(), null, appClassLoader);

        Object appClassLoaderDexPathList = getFieldValueDexPathList(appClassLoader);
        Object patchClassLoaderDexPathList = getFieldValueDexPathList(patchClassLoader);

        if (appClassLoaderDexPathList == null || patchClassLoaderDexPathList == null) {
            return;
        }

        Object appClassLoaderDexElements = getFieldValueDexElements(appClassLoaderDexPathList);
        Object patchClassLoaderDexElements = getFieldValueDexElements(patchClassLoaderDexPathList);

        if (appClassLoaderDexElements == null || patchClassLoaderDexElements == null) {
            return;
        }

        Object mergedDexElements = mergeAppAndPatchDexElements(appClassLoaderDexElements, patchClassLoaderDexElements);
        appClassLoaderDexPathList = getFieldValueDexPathList(appClassLoader);

        setFieldValueDexElements(appClassLoaderDexPathList, mergedDexElements);
        appClassLoader.toString();
    }

    /**
     * 获取BaseDexClassLoader中的成员变量DexPathList pathList
     */
    private Object getFieldValueDexPathList(BaseDexClassLoader baseDexClassLoader) {
        Object dexPathList = null;
        try {
            Field fieldDexPathList = BaseDexClassLoader.class.getDeclaredField("pathList");
            fieldDexPathList.setAccessible(true);
            dexPathList = fieldDexPathList.get(baseDexClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dexPathList;
    }

    /**
     * 获取DexPathList中的成员变量DexPathList$Element[] dexElements
     */
    private Object getFieldValueDexElements(Object dexPathList) {
        Object dexElements = null;
        try {
            Field fieldDexElements = dexPathList.getClass().getDeclaredField("dexElements");
            fieldDexElements.setAccessible(true);
            dexElements = fieldDexElements.get(dexPathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dexElements;
    }

    /**
     * 向DexPathList中的成员变量DexPathList$Element[] dexElements上设置值
     */
    private void setFieldValueDexElements(Object dexPathList, Object dexElements) {
        try {
            Field fieldDexElements = dexPathList.getClass().getDeclaredField("dexElements");
            fieldDexElements.setAccessible(true);
            fieldDexElements.set(dexPathList, dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 将app和patch的dexElements合并，因为实际上这两个都是数组，所以进行的是数组合并
     * 生成合并后的dexElements
     */
    private Object mergeAppAndPatchDexElements(Object appDexElements, Object patchDexElements) {
        // 因为dexElements实际上是个数组，元素类型是DexPathList$Element，所以要获取这个元素类型的class
        Class classElement = appDexElements.getClass().getComponentType();
        int appDexElementsArrayLength = Array.getLength(appDexElements);
        int patchDexElementsArrayLength = Array.getLength(patchDexElements);
        int mergedDexElementsArrayLength = appDexElementsArrayLength + patchDexElementsArrayLength;
        // 合并后的数组，元素都是空的
        Object mergedDexElements = Array.newInstance(classElement, mergedDexElementsArrayLength);
        // 先把patch dex加载后得到的elements放进数组前面，这样会先加载，并因为类加载器的机制，不会再加载后面的有问题的同名代码
        System.arraycopy(patchDexElements, 0, mergedDexElements, 0, patchDexElementsArrayLength);
        // 再把app dex加载后得到的elements放进数组后面
        System.arraycopy(appDexElements, 0, mergedDexElements, patchDexElementsArrayLength, appDexElementsArrayLength);
        return mergedDexElements;
    }
}
