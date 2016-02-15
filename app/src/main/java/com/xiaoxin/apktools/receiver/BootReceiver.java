/*
 *     Copyright (c) 2015 GuDong
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

package com.xiaoxin.apktools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoxin.apktools.Utils.AppInfoEngine;
import com.xiaoxin.apktools.Utils.RxBus;
import com.xiaoxin.apktools.Utils.Utils;
import com.xiaoxin.apktools.bean.AppEntity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by GuDong on 12/7/15 22:49.
 * Contact with 1252768410@qq.com.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getDataString();
        if (TextUtils.isEmpty(packageName)) return;
        if (packageName.contains("package:")) {
            packageName = packageName.replace("package:", "");
        }

         List<AppEntity> erro = new ArrayList<AppEntity>();
        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            AppEntity uninstalledApp = new AppEntity(packageName);
            //TODO 如果已安装的有这个实体类删除 ，如果正在运行的也删除
            for (AppEntity appEntity : Utils.list) {
                if (appEntity.getPackageName().equals(packageName)) {
                    erro.add(appEntity);
                }
            }
            if (erro.size()!=0)
            Utils.list.removeAll(erro);
            for (AppEntity runAppEntity : Utils.runningList) {
                if (runAppEntity.getPackageName().equals(packageName)) {
                    erro.add(runAppEntity);
                }
            }
            if (erro.size()!=0)
            Utils.runningList.removeAll(erro);
            //更新操作
            RxBus.getDefault().post(uninstalledApp);
        } else if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            AppEntity installedEntity = AppInfoEngine.getInstance().getAppByPackageName(packageName);
            if (installedEntity == null) return;
            //如果是已安装就添加在第一个
            Utils.list.set(0, installedEntity);
            RxBus.getDefault().post(installedEntity);
        }
    }
}
