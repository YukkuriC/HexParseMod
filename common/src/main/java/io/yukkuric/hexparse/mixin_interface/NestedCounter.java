package io.yukkuric.hexparse.mixin_interface;

public class NestedCounter {
    static int cntNested = -1;
    static int cntParens = -1;

    // ========== nested list ==========

    public static void EnterNested() {
        cntNested++;
    }

    public static void LeaveNested() {
        cntNested = Math.max(cntNested - 1, -1);
        if (cntNested == -1) cntParens = -1;
    }

    public static int GetNestedCount() {
        return cntNested;
    }

    // ========== paren pattern ==========

    public static void EnterParen() {
        if (cntNested >= 0) cntParens++;
    }

    public static void LeaveParen() {
        if (cntNested >= 0) cntParens = Math.max(cntParens - 1, -1);
    }

    public static int GetParensCount() {
        return cntParens;
    }
}
