package com.nillith.pinyin;

import org.junit.Assert;

import java.util.ArrayList;


public class DictionaryAssembler implements DictCompiler.LineParser {
    private ArrayList<Pinyin> major;
    private static final int ESTIMATED_RECORDS = 28000;
    private static final int ESTIMATED_HETERONYMS = 7000;

    private ArrayList<Heteronym> heteronyms;
    private int generateEmptyCount() {
        Assert.assertTrue(UNINITIALIZED != emptyCount);
        return emptyCount;
    }


    private static final int UNINITIALIZED = -1;
    private int head = UNINITIALIZED;
    private int emptyCount = UNINITIALIZED;


    public DictionaryAssembler() {
        head = UNINITIALIZED;
        emptyCount = 0;
        major = new ArrayList<>(ESTIMATED_RECORDS);
        heteronyms = new ArrayList<>(ESTIMATED_HETERONYMS);
    }

    Dictionary assemble() {
        Dictionary dictionary = new Dictionary();
        dictionary.head = generateDictionaryHead();
        dictionary.major = generateDictionaryMajor();
        dictionary.heteronyms = generateDictionaryHeteronyms();
        dictionary.emptyCount = generateEmptyCount();
        return dictionary;
    }


    private char[] generateDictionaryMajor() {
        char[] arr = new char[major.size()];
        for (int i = 0, end = major.size(); i < end; ++i) {
            arr[i] = major.get(i).data;
        }
        return arr;
    }

    private char[][] generateDictionaryHeteronyms() {
        for (Heteronym h : heteronyms) {
            for (Pinyin p : h.values) {
                Assert.assertTrue(p.data > 0);
                Assert.assertTrue(p.data < Meta.ALL.length);
            }
        }


        char[][] result = new char[heteronyms.size()][];
        for (int i = 0, end = heteronyms.size(); i < end; ++i) {
            result[i] = heteronyms.get(i).toArray();
        }

        for (char[] c : result) {
            for (int i = 1; i < c.length; ++i) {
                Assert.assertTrue(c[i] > 0);
                Assert.assertTrue(c[i] < Meta.ALL.length);
            }
        }
        return result;
    }


    private char generateDictionaryHead() {
        Assert.assertTrue(UNINITIALIZED != head);
        return (char) head;
    }

    private String format(char c) {
        return Integer.toHexString(c) + ":" + c;
    }

    private void pushToMajor(Pinyin pinyin, char c) {
        if (UNINITIALIZED == head) {
            head = c;
        }
        Assert.assertTrue(0 == numPhone);
        major.add(pinyin);
        Assert.assertTrue(String.format("Error at %s: entity value in pinyin.txt must be continuous and in descending sorted order!", format(c)),
                major.size() == (c - head) + 1);
        ++numPhone;
    }

    private void pushEmptyToMajor(char c) {
        if (printLog) {
            System.out.println(format(c) + " has no pinyin!");
        }
        pushToMajor(new Pinyin(), c);
        Assert.assertTrue(UNINITIALIZED != emptyCount);
        emptyCount++;
    }

    boolean printLog;
    int numPhone;
    @Override
    public void parse(int lineNumber, int key, String[] values) {
        numPhone = 0;
        Heteronym heteronym = null;
        String str;
        for (int j = 0, end = values.length; j < end; ++j) {
            str = values[j].trim();

            if (str.equals(DictCompiler.EMPTY_PINYIN_PLACEHOLDER)) {

                pushEmptyToMajor((char) key);
                Assert.assertTrue(values.length == 1);
            } else {
                Pinyin pinyin = Pinyin.parsePinyin(str);
                if (pinyin.toString().equals("")) {
                    if (j == end - 1 && 0 == numPhone) {
                        pushEmptyToMajor((char) key);
                    }
                } else {
                    if (0 == numPhone) {
                        pushToMajor(pinyin, (char) key);
                    } else {
                        if (null == heteronym) {
                            heteronym = new Heteronym();
                            major.get(major.size() - 1).markAsHeteronym();
                        }
                        heteronym.key = (char) key;
                        heteronym.values.add(pinyin);
                        ++numPhone;
                    }
                }
            }
        }

        if (null != heteronym) {
            heteronyms.add(heteronym);
            for (Pinyin p : heteronym.values) {
                Assert.assertTrue(p.data > 0);
                Assert.assertTrue(p.data < Meta.ALL.length);
            }
            Assert.assertTrue(numPhone > 1);
        }
    }
}
