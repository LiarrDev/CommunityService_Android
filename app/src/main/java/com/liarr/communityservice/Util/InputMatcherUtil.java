package com.liarr.communityservice.Util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 输入格式校验工具
 */
public class InputMatcherUtil {

    public static boolean isTel(String tel) {

        boolean isTel = false;

        /*
			兼容号段：
			移动号段：139 138 137 136 135 134 147 150 151 152 157 158 159 178 182 183 184 187 188 198
			联通号段：130 131 132 155 156 166 185 186 145 176
			电信号段：133 153 177 173 180 181 189 199
			虚拟运营商号段：170 171
		 */
        String regTel = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9])|(16[6]))\\d{8}$";
        Pattern pattern = Pattern.compile(regTel);
        Matcher matcher = pattern.matcher(tel);

        if (matcher.matches()) {
            isTel = true;
        }

        return isTel;
    }

    public static boolean isPassword(String password) {

        boolean isPassword = false;

        if (!TextUtils.isEmpty(password.trim()) && password.length() >= 6) {
            isPassword = true;
        }

        return isPassword;
    }

    public static boolean isName(String name) {

        boolean isName = false;

        String regChineseName = "[\u4E00-\u9FA5]{2,4}";
        Pattern patternChineseName = Pattern.compile(regChineseName);
        Matcher matcherChineseName = patternChineseName.matcher(name);
        String regEnglishName = "([A-Z][a-z]*( |$))+";
        Pattern patternEnglishName = Pattern.compile(regEnglishName);
        Matcher matcherEnglishName = patternEnglishName.matcher(name);

        if (matcherChineseName.matches() || matcherEnglishName.matches()) {
            isName = true;
        }

        return isName;
    }
}