/**
 * [Timmess], Java part of Tox Reference Implementation for Android
 * Copyright (C) 2017 Zoff <zoff@zoff.cc>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package com.luseen.autolinklibrary;


import android.content.Context;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiTextViewLinks extends com.vanniktech.emoji.EmojiTextView
{
    private static final int MIN_PHONE_NUMBER_LENGTH = 8;
    private static final int DEFAULT_COLOR = Color.RED;

    private AutoLinkOnClickListener autoLinkOnClickListener;
    private AutoLinkMode[] autoLinkModes;
    private String customRegex;

    private boolean isUnderLineEnabled = false;

    private int mentionModeColor = DEFAULT_COLOR;
    private int hashtagModeColor = DEFAULT_COLOR;
    private int urlModeColor = DEFAULT_COLOR;
    private int phoneModeColor = DEFAULT_COLOR;
    private int emailModeColor = DEFAULT_COLOR;
    private int customModeColor = DEFAULT_COLOR;
    private int defaultSelectedColor = Color.LTGRAY;

    public EmojiTextViewLinks(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public EmojiTextViewLinks(Context context)
    {
        super(context);
    }

    @Override
    public void setHighlightColor(int color)
    {
        super.setHighlightColor(Color.TRANSPARENT);
    }

    public void setAutoLinkText(String text)
    {
        SpannableString spannableString = makeSpannableString(text);
        setText(spannableString);
        setMovementMethod(new LinkTouchMovementMethod());
    }

    private SpannableString makeSpannableString(String text)
    {
        final SpannableString spannableString = new SpannableString(text);
        List<AutoLinkItem> autoLinkItems = matchedRanges(text);

        for (final AutoLinkItem autoLinkItem : autoLinkItems)
        {
            int currentColor = getColorByMode(autoLinkItem.getAutoLinkMode());

            TouchableSpan clickableSpan = new TouchableSpan(currentColor, defaultSelectedColor, isUnderLineEnabled)
            {
                @Override
                public void onClick(View widget)
                {
                    if (autoLinkOnClickListener != null)
                    {
                        autoLinkOnClickListener.onAutoLinkTextClick(autoLinkItem.getAutoLinkMode(), autoLinkItem.getMatchedText());
                    }
                }
            };

            spannableString.setSpan(clickableSpan, autoLinkItem.getStartPoint(), autoLinkItem.getEndPoint(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }

    private List<AutoLinkItem> matchedRanges(String text)
    {

        List<AutoLinkItem> autoLinkItems = new LinkedList<>();

        if (autoLinkModes == null)
        {
            autoLinkModes = new AutoLinkMode[]{AutoLinkMode.MODE_URL};
            // throw new NullPointerException("Please add at least one mode");
        }

        for (AutoLinkMode anAutoLinkMode : autoLinkModes)
        {
            String regex = Utils.getRegexByAutoLinkMode(anAutoLinkMode, customRegex);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            if (anAutoLinkMode == AutoLinkMode.MODE_PHONE)
            {
                while (matcher.find())
                {
                    if (matcher.group().length() > MIN_PHONE_NUMBER_LENGTH)
                    {
                        autoLinkItems.add(new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                    }
                }
            }
            else
            {
                while (matcher.find())
                {
                    autoLinkItems.add(new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                }
            }
        }

        return autoLinkItems;
    }

    private int getColorByMode(AutoLinkMode autoLinkMode)
    {
        switch (autoLinkMode)
        {
            case MODE_HASHTAG:
                return hashtagModeColor;
            case MODE_MENTION:
                return mentionModeColor;
            case MODE_URL:
                return urlModeColor;
            case MODE_PHONE:
                return phoneModeColor;
            case MODE_EMAIL:
                return emailModeColor;
            case MODE_CUSTOM:
                return customModeColor;
            default:
                return DEFAULT_COLOR;
        }
    }

    public void setMentionModeColor(@ColorInt int mentionModeColor)
    {
        this.mentionModeColor = mentionModeColor;
    }

    public void setHashtagModeColor(@ColorInt int hashtagModeColor)
    {
        this.hashtagModeColor = hashtagModeColor;
    }

    public void setUrlModeColor(@ColorInt int urlModeColor)
    {
        this.urlModeColor = urlModeColor;
    }

    public void setPhoneModeColor(@ColorInt int phoneModeColor)
    {
        this.phoneModeColor = phoneModeColor;
    }

    public void setEmailModeColor(@ColorInt int emailModeColor)
    {
        this.emailModeColor = emailModeColor;
    }

    public void setCustomModeColor(@ColorInt int customModeColor)
    {
        this.customModeColor = customModeColor;
    }

    public void setSelectedStateColor(@ColorInt int defaultSelectedColor)
    {
        this.defaultSelectedColor = defaultSelectedColor;
    }

    public void addAutoLinkMode(AutoLinkMode... autoLinkModes)
    {
        this.autoLinkModes = autoLinkModes;
    }

    public void setCustomRegex(String regex)
    {
        this.customRegex = regex;
    }

    public void setAutoLinkOnClickListener(AutoLinkOnClickListener autoLinkOnClickListener)
    {
        this.autoLinkOnClickListener = autoLinkOnClickListener;
    }

    public void enableUnderLine()
    {
        isUnderLineEnabled = true;
    }
}
