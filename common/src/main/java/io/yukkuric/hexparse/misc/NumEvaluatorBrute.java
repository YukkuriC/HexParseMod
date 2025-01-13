package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.api.spell.iota.DoubleIota;

import java.util.ArrayList;
import java.util.Collections;

public class NumEvaluatorBrute {
    static double TOLERANCE = DoubleIota.TOLERANCE / 10;
    static double MAX_VALUE = Long.MAX_VALUE;
    static int MAX_DECIMAL_REACH = 100;

    public static String getAnglesFromNum(double target) {
        return getAnglesFromNum(target, TOLERANCE);
    }

    public static String getAnglesFromNum(Double target, double tolerance) {
        if (target.isNaN()) target = (double) 0;
        var neg = target < 0;
        target = Math.min(Math.abs(target), MAX_VALUE);
        var seqList = new ArrayList<String>();

        // 1. handle decimal
        for (var i = 0; i < MAX_DECIMAL_REACH; i++) {
            if (target % 1 < tolerance) break;
            target *= 2;
            tolerance *= 2;
            seqList.add("d");
        }

        // 2. bit deconstruct
        long n = Math.round(target);
        while (n != 0) {
            if ((n & 10) == 10) {
                seqList.add("e");
                n ^= 10;
            } else if ((n & 5) == 5) {
                seqList.add("q");
                n ^= 5;
            } else if ((n & 1) == 1) {
                seqList.add("w");
                n ^= 1;
            } else {
                seqList.add("a");
                n >>>= 1;
            }
        }
        seqList.add(neg ? "dedd" : "aqaa");
        Collections.reverse(seqList);
        return String.join("", seqList);
    }
}
