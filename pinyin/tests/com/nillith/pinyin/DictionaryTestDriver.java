package com.nillith.pinyin;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class DictionaryTestDriver {

    private static Dictionary dictionary = Dictionary.getInstance();
    private static char head = dictionary.head;
    private static char tail = (char) (dictionary.major.length + head - 1);

    @BeforeClass
    public static void loadDictionary() {
        Assert.assertNotNull(dictionary);
        Assert.assertNotNull(dictionary.major);
        Assert.assertNotNull(dictionary.heteronyms);
        System.out.println(String.format("Records counts: %d.", dictionary.major.length));
        System.out.println(String.format("Heteronyms counts: %d.", dictionary.heteronyms.length));
        System.out.println(String.format("Empty counts: %d.", dictionary.emptyCount));
        Assert.assertEquals(dictionary.major.length, tail - head + 1);
    }

    private void assertTrue(char c, boolean b) {
        Assert.assertTrue(Messenger.charInfo(c), b);
    }

    private void assertFalse(char c, boolean b) {
        Assert.assertFalse(Messenger.charInfo(c), b);
    }

    @Test
    public void heteronymsHasPinyinData() {
        char[][] heteronyms = dictionary.heteronyms;
        String failMsg;
        for (int i = 0; i < heteronyms.length; ++i) {
            failMsg = "i:" + i;
            Assert.assertNotNull(failMsg, heteronyms[i]);
            Assert.assertTrue(failMsg, heteronyms[i].length > 1);
        }
    }

    @Test
    public void heteronymsIsSorted() {
        char[][] heteronyms = dictionary.heteronyms;
        if (heteronyms.length < 2) {
            return;
        }
        char[] left = heteronyms[0];
        Assert.assertTrue(left.length > 1);
        char[] right;
        for (int i = 1; i < heteronyms.length; ++i) {
            right = heteronyms[i];
            Assert.assertTrue(left[0] < right[0]);
            left = right;
        }
    }

    @Test
    public void getExtrasTest() {
        int heteronymCount = 0;
        char[] extra;
        for (char c = head; c <= tail; ++c) {
            extra = dictionary.getExtras(c);
            assertTrue(c, null != extra);
            if (extra.length > 0) {
                assertTrue(c, c == extra[0]);
                ++heteronymCount;
            }
        }
        Assert.assertEquals(dictionary.heteronyms.length, heteronymCount);
    }

    @Test
    public void majorToHeteronymsTest() {
        Pinyin pinyin = new Pinyin();
        char[] extra;
        for (char c = head; c <= tail; ++c) {
            pinyin.unsafeSetInternal(dictionary.get(c));
            extra = dictionary.getExtras(c);
            Assert.assertNotNull(extra);
            if (pinyin.isMarkedAsHeteronym()) {
                assertTrue(c, extra.length > 1);
                Assert.assertEquals(c, extra[0]);
            } else {
                assertTrue(c, extra.length == 0);
            }
        }
    }

    @Test
    public void heteronymsToMajorTest() {
        char[][] heteronyms = dictionary.heteronyms;
        char c;
        Pinyin pinyin = new Pinyin();
        for (char[] heteronym : heteronyms) {
            c = heteronym[0];
            pinyin.unsafeSetInternal(dictionary.get(c));
            assertTrue(c, pinyin.isMarkedAsHeteronym());
        }
    }

    @Test
    public void containsTest() {
        char c;
        for (int i = 0; i <= Character.MAX_VALUE; ++i) {
            c = (char) i;
            if (c < head || c > tail) {
                assertFalse(c, dictionary.contains(c));
            } else {
                assertTrue(c, dictionary.contains(c));
            }
        }
    }

    @Test
    public void conversionTest() {
        char c;
        for (int i = 0; i <= Character.MAX_VALUE; ++i) {
            c = (char) i;
            Assert.assertEquals(c, dictionary.toCharacter(dictionary.toIndex(c)));
        }
    }

    @Test
    public void emptyCountTest() {
        char emptyCount = 0;
        Pinyin pinyin = new Pinyin();
        for (char c = head; c <= tail; ++c) {
            pinyin.unsafeSetInternal(dictionary.get(c));
            if (pinyin.toString().equals("")) {
                emptyCount++;
            }
        }
        Assert.assertEquals(dictionary.emptyCount, emptyCount);
    }

}
