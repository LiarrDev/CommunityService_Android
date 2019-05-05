package com.liarr.communityservice.Util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    /**
     * List 去重
     *
     * @param list 需要去重的 List
     * @return 去重后的 List
     */
    public static List removeDuplicate(List list) {
        List listTemp = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (!listTemp.contains(list.get(i))) {
                listTemp.add(list.get(i));
            }
        }
        return listTemp;
    }
}