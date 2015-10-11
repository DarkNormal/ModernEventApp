package com.lordan.mark.PosseUp;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by Mark on 9/26/2015.
 * Language Helper used for testing different locales without changing phone language
 * Based on code by Agis guillaume
 * Available at: https://github.com/skategui/tutorial_blog/blob/master/language/LanguageHelper.java
 * Accessed 26/09/2015
 */
public class LanguageHelper {

    public static void changeLocale(Resources res, String locale){
        Configuration config = new Configuration(res.getConfiguration());

        /*Switch statement receives Locale from activity, decision made from locale string
        * English is default, Spanish, French and German are supported
        */
        switch(locale){
            case "de":
                config.locale = Locale.GERMAN;
                break;
            case "es":
                config.locale = new Locale("es", "ES");
                break;
            case "fr":
                config.locale = Locale.FRENCH;
                break;
            default:
                config.locale = Locale.ENGLISH;
                break;

        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
