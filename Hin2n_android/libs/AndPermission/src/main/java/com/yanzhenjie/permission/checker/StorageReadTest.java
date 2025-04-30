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

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import java.lang.reflect.Method;
import java.io.File;

/**
 * Created by Zhenjie Yan on 2018/1/16.
 */
class StorageReadTest implements PermissionTest {

    StorageReadTest() {
    }

    @Override
    public boolean test() throws Throwable {
        // 判断是否为 Android 10（API 29）及以上
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                // 通过反射调用 isExternalStorageLegacy() 方法
                Method method = Environment.class.getMethod("isExternalStorageLegacy");
                boolean isLegacy = (Boolean) method.invoke(null);
                // 如果不是 legacy 模式，返回 true（不需要权限）
                if (!isLegacy) return true;
            } catch (Exception ignored) {
                // 如果反射失败，假设不是 legacy 模式，继续后续检查
            }
        }

        // 如果外部存储没有挂载好，则返回 true（不需要权限）
        if (!TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState()))
            return true;

        // 获取外部存储目录
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) return true;

        // 判断是否有读取目录信息权限
        long modified = directory.lastModified();
        String[] pathList = directory.list();
        return modified > 0 && pathList != null;
    }
}
