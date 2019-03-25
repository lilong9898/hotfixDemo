class FixMainDexExtension {
    FixMainDexMode mode
    /** 禁止放入maindex中的包名*/
    String[] maindexExcludedPackages
}

/**
 * 可在两种模式中选一种:
 * (1) 给maindex补充缺少的内部类
 * (2) 从maindex中去掉多余的类(用于maindex overflow的情况)
 * */
enum FixMainDexMode {
    ADD_INNER_CLASS,
    REMOVE_EXCLUDED_PACKAGES
}