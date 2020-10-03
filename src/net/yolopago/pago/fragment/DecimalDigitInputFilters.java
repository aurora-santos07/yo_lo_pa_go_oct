package net.yolopago.pago.fragment;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DecimalDigitInputFilters implements InputFilter {
    Pattern mPattern;

    public DecimalDigitInputFilters(int digitosAntesPunto, int digitosDespuesPunto) {
        String pattern=String.format("[0-9]{0,%d}+((\\.[0-9]{0,%d})?)|(\\.)?",(digitosAntesPunto-1),(digitosDespuesPunto-1));
        mPattern = Pattern.compile(pattern);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dent){
        Matcher matcher = mPattern.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }
}
