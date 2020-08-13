package com.example.myuber;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("fKcdTHf66OY2EPx51t2KiK6hFO8nGLL20JLbGx4x")
                // if defined
                .clientKey("o69A2MeMoc8Ez7EQTimFeMHJWnHoFQ1MeAMgyfsM")
                .server("https://parseapi.back4app.com/")
                        .build()
        );

    }
}
