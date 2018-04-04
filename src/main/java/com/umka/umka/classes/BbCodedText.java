package com.umka.umka.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by trablone on 12/17/16.
 */

public class BbCodedText {

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<>();

    static {
        //addPattern(emoticons, ":)", R.drawable.smile_face);
        //addPattern(emoticons, ":))", R.drawable.smile_very_face);
        //addPattern(emoticons, ":)))", R.drawable.joy_face);
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile, int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    private static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;

                    Drawable emoji = ContextCompat.getDrawable(context, entry.getValue());
                    emoji.setBounds(0,0, 30, 30);


                    spannable.setSpan(
                            new ImageSpan(emoji, ImageSpan.ALIGN_BASELINE),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }
        }

        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }
}
