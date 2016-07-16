package com.nillith.pinyin;

import java.security.SecureRandom;
import java.util.Random;

class TestValueGen {
    private interface RandomCharGen {
        char nextChar();
    }

    private char dictHead;
    private int dictSize;
    private Random rng = new SecureRandom();

    TestValueGen(char dictHead, int dictSize) {
        this.dictHead = dictHead;
        this.dictSize = dictSize;
        rng.setSeed(System.nanoTime());
    }

    private String getRandomString(int maxLengthExclude, RandomCharGen charGen) {
        int size = rng.nextInt(maxLengthExclude);
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; ++i) {
            sb.append(charGen.nextChar());
        }
        return sb.toString();
    }


    String getRandomDictionaryString(int maxLength) {
        return getRandomString(maxLength, new RandomCharGen() {
            @Override
            public char nextChar() {
                return (char) (rng.nextInt(dictSize) + dictHead);
            }
        });
    }

    String getDictionaryCompleteSequence() {
        StringBuilder sb = new StringBuilder(dictSize);
        for (int i = 0; i < dictSize; ++i) {
            sb.append((char) (i + dictHead));
        }
        rng.nextInt(3);
        return sb.toString();
    }

    String getRandomCharString(int maxLength) {
        return getRandomString(maxLength, new RandomCharGen() {
            @Override
            public char nextChar() {
                return (char) rng.nextInt(Character.MAX_VALUE + 1);
            }
        });
    }
}
