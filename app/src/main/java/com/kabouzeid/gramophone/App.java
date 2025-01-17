package com.kabouzeid.gramophone;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.kabouzeid.gramophone.appshortcuts.DynamicShortcutManager;
import com.kabouzeid.gramophone.util.PreferenceUtil;

import chr_56.MDthemer.core.ThemeColor;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class App extends Application {

    private static App app;
    public int theme;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        // default theme
        if (!ThemeColor.isConfigured(this, 1)) {
            ThemeColor.editTheme(this)
                    .primaryColorRes(R.color.md_blue_A400)
                    .accentColorRes(R.color.md_yellow_900)
                    .commit();
        }

        // Set up dynamic shortcuts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            new DynamicShortcutManager(this).initDynamicShortcuts();
        }

        //
        theme = PreferenceUtil.getInstance(this).getGeneralTheme();

    }

    public static App getInstance() {
        return app;
    }
    public Boolean nightmode(){
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
            default:
                return false;
        }
    }

}
