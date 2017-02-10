package net.grandcentrix.thirtyinch.viewmodel;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LocalizedString {

    private static final LocalizedString EMPTY = new LocalizedString("");
    private String mHardcodedString;
    private FormattedString mFormattedString;

    private LocalizedString(@NonNull final String hardcodedString) {
        if (hardcodedString == null) {
            throw new IllegalArgumentException("the string cannot be null");
        }
        this.mHardcodedString = hardcodedString;
    }

    private LocalizedString(@StringRes final int stringResId, final Object... formatArgs) {
        mFormattedString = new FormattedString(stringResId, formatArgs);
    }

    public static LocalizedString create(@Nullable final String hardcodedString) {
        if (hardcodedString == null) {
            // This is a valid conversion because TextViews do this conversion, too.
            return LocalizedString.empty();
        } else {
            return new LocalizedString(hardcodedString);
        }
    }

    public static LocalizedString empty() {
        return EMPTY;
    }

    public static LocalizedString create(@StringRes final int stringResId, final Object... formatArgs) {
        return new LocalizedString(stringResId, formatArgs);
    }

    public boolean isEmpty() {
        if (mHardcodedString != null) {
            return mHardcodedString.equals("");
        }

        // !!!Dangerous assumption!!!
        // -> formatted strings are never empty
        return false;
    }

    public String toString() {
        if (mHardcodedString != null) {
            return mHardcodedString;
        }
        return mFormattedString.toString();
    }

    public String getString(final Context context) {
        if (mHardcodedString != null) {
            return mHardcodedString;
        }
        return mFormattedString.getString(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalizedString)) return false;

        LocalizedString that = (LocalizedString) o;

        if (mHardcodedString != null ? !mHardcodedString.equals(that.mHardcodedString) : that.mHardcodedString != null)
            return false;
        return mFormattedString != null ? mFormattedString.equals(that.mFormattedString) : that.mFormattedString == null;

    }

    @Override
    public int hashCode() {
        int result = mHardcodedString != null ? mHardcodedString.hashCode() : 0;
        result = 31 * result + (mFormattedString != null ? mFormattedString.hashCode() : 0);
        return result;
    }

    private class FormattedString {

        private final int mStringResId;
        private final Object[] mFormatArgs;

        public FormattedString(@StringRes final int stringResId, final Object... formatArgs) {
            mStringResId = stringResId;
            mFormatArgs = formatArgs;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();

            sb.append("0x");
            sb.append(Integer.toHexString(mStringResId));

            if (mFormatArgs == null || mFormatArgs.length == 0) {
                return sb.toString();
            }

            final List<Object> argList = Arrays.asList(mFormatArgs);

            sb.append("[");
            for (Iterator<Object> iterator = argList.iterator(); iterator.hasNext(); ) {
                sb.append(String.valueOf(iterator.next()));
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("]");

            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FormattedString)) return false;

            FormattedString that = (FormattedString) o;

            if (mStringResId != that.mStringResId) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(mFormatArgs, that.mFormatArgs);

        }

        @Override
        public int hashCode() {
            int result = mStringResId;
            result = 31 * result + Arrays.hashCode(mFormatArgs);
            return result;
        }

        public String getString(final Context context) {
            return context.getString(mStringResId, mFormatArgs);
        }
    }

}
