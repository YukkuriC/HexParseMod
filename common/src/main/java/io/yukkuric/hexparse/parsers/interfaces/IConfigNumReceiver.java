package io.yukkuric.hexparse.parsers.interfaces;

public interface IConfigNumReceiver {
    default void receiveConfigNum(int configNum) {
    }
    default boolean hasConfigNum(int mine, int comparer) {
        return (mine & comparer) == comparer;
    }
}
