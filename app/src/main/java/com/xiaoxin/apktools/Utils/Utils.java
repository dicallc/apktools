package com.xiaoxin.apktools.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.xiaoxin.apktools.R;
import com.xiaoxin.apktools.bean.AppEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/1/28.
 */
public class Utils {
    public static List<AppEntity> list = new ArrayList<AppEntity>();
    public static List<AppEntity> runningList = new ArrayList<>();

    public static void putStringPreference(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, def);
    }

    /**
     * 获取当前主题色对应色值
     *
     * @param context
     * @return
     */
    public static int getThemePrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.theme_color, typedValue, true);
        return typedValue.data;
    }

    /**
     * running list is show AppPlus or not
     *
     * @param context Context
     * @return return true if recent list view need show appplus
     */
    public static boolean isShowSelf(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.switch_preference_key_show_self), false);
    }

    /**
     * list item is brief mode or not
     *
     * @param context Context
     * @return return true if brief mode else not
     */
    public static boolean isBriefMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.switch_preference_key_list_item_brief_mode), true);
    }

    /**
     * 获取color对应的int值
     *
     * @param context Activity
     * @param color   资源颜色id
     * @return 对应的int value
     */
    public static int getColorWarp(Activity context, @ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }

    public static boolean isOwnApp(Activity activity, String packageName) {
        return activity.getPackageName().equals(packageName);
    }
    private static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }
    /**
     * check current language is chinese or not
     *
     * @return true if it is else return false
     */
    public static boolean isChineseLanguage() {
        String language = getLanguageEnv();
        if (language != null && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))
            return true;
        else
            return false;
    }
    /**
     * 获取主题强调色
     *
     * @param context
     * @return
     */
    public static int getAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.theme_accent_color, typedValue, true);
        return typedValue.data;
    }
}
