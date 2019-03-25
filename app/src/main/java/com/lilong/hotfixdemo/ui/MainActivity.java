package com.lilong.hotfixdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lilong.hotfixdemo.R;
import com.lilong.hotfixdemo.another.dex.ClassInAnotherDex;
import com.lilong.hotfixdemo.fixed.Util;

public class MainActivity extends AppCompatActivity {

    private Button mBtnCallMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("" + ClassInAnotherDex.class);
        setContentView(R.layout.activity_main);
        mBtnCallMethod = (Button) findViewById(R.id.btnCallMethod);
        mBtnCallMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 在5.0以下手机上, 即使用dalvik vm的手机上, 会有dexopt过程, 这个过程会导致, 调用patch dex中的方法时, 出现下面的崩溃:
                 *
                 * process: com.lilong.hotfixdemo, PID: 1876
                 *     java.lang.IllegalAccessError: Class ref in pre-verified class resolved to unexpected implementation
                 *         at com.lilong.hotfixdemo.ui.MainActivity$1.onClick(MainActivity.java:23)
                 *         at android.view.View.performClick(View.java:4438)
                 *         at android.view.View$PerformClick.run(View.java:18422)
                 *         at android.os.Handler.handleCallback(Handler.java:733)
                 *         at android.os.Handler.dispatchMessage(Handler.java:95)
                 *         at android.os.Looper.loop(Looper.java:136)
                 *         at android.app.ActivityThread.main(ActivityThread.java:5001)
                 *         at java.lang.reflect.Method.invokeNative(Native Method)
                 *         at java.lang.reflect.Method.invoke(Method.java:515)
                 *         at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:785)
                 *         at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:601)
                 *         at dalvik.system.NativeStart.main(Native Method)
                 *  原因: dexopt过程中, 如果某个类中所有引用的类(只考虑这一层引用), 都和它本身处于同一个dex中, 则这个类会被打上CLASS_ISPREVERIFIED标记
                 *  因为hotfix后, com.lilong.hotfixdemo.fixed.Util类在fixedDex中, 跟MainActivity不是同一个dex, 而MainActivity已经被打上CLASS_ISPREVERIFIED标记了, 所以会出现这个崩溃
                 *
                 *  这个检测是google为了防止外部DEX注入的一个安全方案，即保证运行期的Class与其直接引用类之间所在的DEX关系要与安装时候一致
                 *
                 *  绕过:
                 *  建造一个单独的dex, 让所有类都引用这个dex中的类, 这样所有类都会包含非本dex中的引用, 就都不会被打上CLASS_ISPREVERIFIED标记
                 *  比如这里{@link ClassInAnotherDex}类被{@link MainActivity}类引用, 但本身被强制放在非mainDex里,
                 *  这就使得{@link MainActivity}引用了不在同一个dex中的类, 使得它不会被打上CLASS_ISPREVERIFIED标记, 运行就不会崩溃
                 * */
                Util.showToast();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
