package net.yolopago.pago.utilities;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {
    private static final String TAG = "DecimalDigitsInputFilte";
    Pattern mPattern;

    public DecimalDigitsInputFilter(int enteros,int decimales) {
        String pattern=String.format("[0-9]{0,%d}+((\\.[0-9]{0,%d})?)|(\\.)?",enteros,decimales);
        mPattern= Pattern.compile(pattern);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Log.d(TAG, "filter: Sour:"+source.toString()+" - span:"+dest.toString()+" - strat:"+ dstart+ " - end:"+dend);
        String actual= dest.toString();
        String str1="";
        String str2="";
        if(dstart==dend){
            if (dstart==actual.length()) {
                str1=actual;
            }else{
                str1 = actual.substring(0, dstart);
                str2 = actual.substring(dstart, actual.length() - 1);
            }
        }else{
            str1=actual.substring(0,dstart);
            str2=actual.substring(dend-1,actual.length()-1);
        }
        actual=str1+source+str2;
        Matcher matcher=mPattern.matcher(actual);
        if(!matcher.matches())
            return "";
        return null;
    }

}
