package com.app.cabscout.model.custom;

import com.app.cabscout.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
      //  FontsOverride.setDefaultFont(this, "MONOSPACE", "Mark Simonson - Proxima Nova Semibold_0.ttf");

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Mark Simonson - Proxima Nova Semibold_0.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}