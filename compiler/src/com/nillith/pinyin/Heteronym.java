package com.nillith.pinyin;

import java.util.ArrayList;


public class Heteronym {
    char key;
    ArrayList<Pinyin> values = new ArrayList<>();

    char[] toArray() {
        char[] arr = new char[values.size() + 1];
        arr[0] = key;
        for (int i = 0, end = values.size(); i < end; ++i) {
            arr[i + 1] = values.get(i).data;
        }
        return arr;
    }
}
