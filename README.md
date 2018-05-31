# 热修复演示
本demo演示了如何实现最简单的热修复

## 演示

![](./demo/entrance.png) ![](./demo/without_hotfix.png) ![](./demo/with_hotfix.png)

- 启动后显示左图页面，点击”直接进“，在打开的页面中点击”调用方法“，会弹出toast "this is a bad method"，如中间图所示
- 启动后显示左图页面，点击”热修复一下再进“，在打开的页面中点击”调用方法“，会弹出toast "this is a good method"，如右图所示

## 如何运行
1. 通过Android Studio的package manager安装23.0.2版本的build-tools，和android 25 platform
2. 项目使用的gradle版本是3.5，android gradle plugin版本是2.2.0，如果没装的话可以提前下载
3. 打开项目，执行gradle sync以下载必要的依赖，完成后的工程结构:
