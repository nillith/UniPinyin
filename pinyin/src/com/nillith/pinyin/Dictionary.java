package com.nillith.pinyin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;


public class Dictionary implements Serializable {

    private static Dictionary instance;

    public static Dictionary getInstance() {
        if (null == instance) {
            synchronized (Dictionary.class) {
                if (null == instance) {
                    instance = load();
                }
            }
        }
        return instance;
    }

    static final long serialVersionUID = -6681431519240003099L;
    private static final String DICTIONARY_BIN = "dictionary.bin";
    private static final char[] EMPTY = {};
    char head;
    int emptyCount;
    char[] major;
    char[][] heteronyms;

    static Dictionary load() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(Dictionary.class.getClassLoader().getResourceAsStream(DICTIONARY_BIN));
            return (Dictionary) ois.readObject();
        } catch (Exception e) {
            return null;
        } finally {
            if (null != ois) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    int toIndex(char c) {
        return c - head;
    }

    char toCharacter(int index) {
        return (char) (index + head);
    }

    public boolean contains(char c) {
        return contains(toIndex(c));
    }

    private boolean contains(int idx) {
        return idx >= 0 && idx < major.length;
    }

    public char get(char c) {
        int idx = toIndex(c);
        return contains(idx) ? major[idx] : 0;
    }

    public char[] getExtras(char c) {
        char[] cArr = {c};
        int i = Arrays.binarySearch(heteronyms, cArr, new Comparator<char[]>() {
            public int compare(char[] o1, char[] o2) {
                return o1[0] - o2[0];
            }
        });

        return i < 0 ? EMPTY : heteronyms[i];
    }
}
