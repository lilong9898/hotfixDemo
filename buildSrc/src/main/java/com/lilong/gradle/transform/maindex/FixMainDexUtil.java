package com.lilong.gradle.transform.maindex;

import java.io.File;

public class FixMainDexUtil {

    public interface OnFileTraversedCallback {
        void onFileTraversed(File f);
    }

    public static void traverseFileTreeByDFS(File root, OnFileTraversedCallback callback) {

        if (root == null || !root.exists()) {
            return;
        }

        traverseFileTreeByDFSInner(root, callback);
    }

    private static void traverseFileTreeByDFSInner(File root, OnFileTraversedCallback callback) {

        if (root == null || !root.exists()) {
            return;
        }

        if (root.isFile()) {
            if (callback != null) {
                callback.onFileTraversed(root);
            }
            return;
        } else if (root.isDirectory()) {
            if (root.listFiles() == null) {
                return;
            } else {
                for (File subFile : root.listFiles()) {
                    traverseFileTreeByDFSInner(subFile, callback);
                }
            }
        }

    }
}
