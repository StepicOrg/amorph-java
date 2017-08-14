package org.stepik.amorph.utils;

import com.google.common.collect.Sets;
import org.simmetrics.StringMetrics;

import java.util.Map;
import java.util.Set;

public class MapStringDistance {
    public static double compare(Map first, Map second) {
        Set firstKeys = first.keySet();
        Set secondKeys = second.keySet();
        Set commonKeys = Sets.intersection(firstKeys, secondKeys);

        double total = 0;
        for (Object key : commonKeys) {
            total += StringMetrics.qGramsDistance().compare(first.get(key).toString(), second.get(key).toString());
        }

        return 2.0 * total / (double)(firstKeys.size() + secondKeys.size());
    }
}
