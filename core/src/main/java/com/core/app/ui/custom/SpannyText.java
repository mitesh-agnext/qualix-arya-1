package com.core.app.ui.custom;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

public class SpannyText extends SpannableStringBuilder {

    private int flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;

    public SpannyText() {
        super("");
    }

    public SpannyText(CharSequence text) {
        super(text);
    }

    public SpannyText(CharSequence text, Object... spans) {
        super(text);
        for (Object span : spans) {
            setSpan(span, 0, length());
        }
    }

    public SpannyText(CharSequence text, Object span) {
        super(text);
        setSpan(span, 0, text.length());
    }

    public SpannyText append(CharSequence text, Object... spans) {
        append(text);
        for (Object span : spans) {
            setSpan(span, length() - text.length(), length());
        }
        return this;
    }

    public SpannyText append(CharSequence text, Object span) {
        append(text);
        setSpan(span, length() - text.length(), length());
        return this;
    }

    public SpannyText append(CharSequence text, ImageSpan imageSpan) {
        text = "." + text;
        append(text);
        setSpan(imageSpan, length() - text.length(), length() - text.length() + 1);
        return this;
    }

    @Override
    public SpannyText append(CharSequence text) {
        super.append(text);
        return this;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private void setSpan(Object span, int start, int end) {
        setSpan(span, start, end, flag);
    }

    public SpannyText findAndSpan(CharSequence textToSpan, GetSpan getSpan) {
        int lastIndex = 0;
        while (lastIndex != -1) {
            lastIndex = toString().indexOf(textToSpan.toString(), lastIndex);
            if (lastIndex != -1) {
                setSpan(getSpan.getSpan(), lastIndex, lastIndex + textToSpan.length());
                lastIndex += textToSpan.length();
            }
        }
        return this;
    }

    public interface GetSpan {

        Object getSpan();

    }

    public static SpannableString spanText(CharSequence text, Object... spans) {
        SpannableString spannableString = new SpannableString(text);
        for (Object span : spans) {
            spannableString.setSpan(span, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public static SpannableString spanText(CharSequence text, Object span) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(span, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}