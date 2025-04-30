/*
 * Copyright © Zhenjie Yan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.permission.checker;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import java.lang.reflect.Method;
import java.io.File;

/**
 * Created by Zhenjie Yan on 2018/1/16.
 */
class StorageWriteTest implements PermissionTest {

    private Context mContext;

    StorageWriteTest(Context c) {
        mContext = c;
    }

    @Override
    public boolean test() throws Throwable {
        // 如果是 Android 10（API 29）及以上，尝试判断是否处于 legacy 模式
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                // 使用反射调用 Environment.isExternalStorageLegacy()
                Method method = Environment.class.getMethod("isExternalStorageLegacy");
                boolean isLegacy = (Boolean) method.invoke(null);
                if (!isLegacy) return true;
            } catch (Exception ignored) {
                // 如果反射失败，假设不是 legacy 模式，继续检测权限
            }
        }

        // 如果外部存储未挂载，则返回 true
        if (!TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState()))
            return true;

        // 获取外部文件目录
        File directory = mContext.getExternalFilesDir(null);
        if (directory == null || !directory.exists()) return true;

        // 在外部文件目录下创建一个临时目录 Android
        File parent = new File(directory, "Android");
        if (parent.exists() && parent.isFile()) {
            if (!parent.delete()) return false;
        }
        if (!parent.exists()) {
            if (!parent.mkdirs()) return false;
        }

        // 创建测试文件
        File file = new File(parent, "ANDROID.PERMISSION.TEST");
        if (file.exists()) {
            return file.delete();
        } else {
            return file.createNewFile();
        }
    }
}
