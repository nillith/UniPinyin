package com.nillith.pinyin;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class PinyinTestDriver {

    private static Dictionary dictionary = Dictionary.getInstance();
    private static TestValueGen valueGen;
    private static DictCompiler pdc = new DictCompiler();

    private abstract class AbsCharTester {
        void beforeTraverse() {

        }

        void afterTraverse() {

        }

        abstract void test(char c);
    }

    @BeforeClass
    public static void initialize() {
        Assert.assertNotNull(dictionary);
        Assert.assertNotNull(dictionary.major);
        Assert.assertNotNull(dictionary.heteronyms);
        valueGen = new TestValueGen(dictionary.head, dictionary.major.length);
    }


    @Test
    public void srcMatchTest() {
        pdc.parsePinyinSrc(new DictCompiler.LineParser() {
            char c;
            Pinyin[] pinyins;

            @Override
            public void parse(int lineNumber, int key, String[] values) {
                c = (char) key;
                pinyins = Pinyin.getPinyinAll(c);
                Assert.assertEquals(Messenger.charInfo(c), values.length, pinyins.length);
                for (int i = 0; i < values.length; ++i) {
                    if (values[i].equals(DictCompiler.EMPTY_PINYIN_PLACEHOLDER)) {
                        values[i] = "";
                    }
                    Assert.assertTrue(Messenger.charInfo(c),
                            values[i].equals(pinyins[i].toString())
                                    || values[i].equals(pinyins[i].toStringAscii()));
                }
            }
        });
    }

    @Test
    public void parsePinyinTest() {
        for (Meta m : Meta.ALL) {
            parseCompare(m.toString(), m);
            parseCompare(m.toStringAscii(), m);
        }
    }


    private void parseCompare(String parsee, Meta comp) {
        Pinyin pinyin = Pinyin.parsePinyin(parsee);
        Assert.assertSame(comp, Meta.get(pinyin.getMetaIndex()));
    }

    private void traverseDictionary(AbsCharTester tester) {
        tester.beforeTraverse();
        for (int i = 0; i < dictionary.major.length; ++i) {
            tester.test((char) (i + dictionary.head));
        }
        tester.afterTraverse();
    }

    @Test
    public void emptyCountTest() {

        traverseDictionary(new AbsCharTester() {
            char emptyCount = 0;
            Pinyin pinyin;

            @Override
            void test(char c) {
                pinyin = Pinyin.getPinyin(c);
                if (pinyin.toString().equals("")) {
                    Assert.assertEquals("", pinyin.toString());
                    emptyCount++;
                }
            }

            @Override
            void afterTraverse() {
                Assert.assertEquals(dictionary.emptyCount, emptyCount);
            }
        });
    }


    @Test
    public void getPinyinConsistencyTest() {
        traverseDictionary(new AbsCharTester() {
            @Override
            void test(char c) {
                Assert.assertEquals(Pinyin.getPinyin(c).toString(), Pinyin.getPinyinString(c));
                Assert.assertEquals(Pinyin.getPinyin(c).toStringAscii(), Pinyin.getPinyinStringAscii(c));
                Assert.assertEquals(Pinyin.getPinyin(c).toStringAsciiNoTone(), Pinyin.getPinyinStringAsciiNoTone(c));
            }
        });
    }

    @Test
    public void getPinyinDictionaryCompleteSequenceTest() {
        stringGetPinyinTest(valueGen.getDictionaryCompleteSequence());
    }

    @Test
    public void randomCharSequenceTest() {
        for (int i = 0; i < 1000; ++i) {
            stringGetPinyinTest(valueGen.getRandomCharString(1000));
        }
    }

    @Test
    public void randomDictionarySequenceTest() {
        for (int i = 0; i < 1000; ++i) {
            stringGetPinyinTest(valueGen.getRandomDictionaryString(1000));
        }
    }

    private void stringGetPinyinTest(Pinyin[] pinyins, String input, int resultOption) {
        StringBuilder sb = new StringBuilder(pinyins.length * 5);
        for (Pinyin pinyin : pinyins) {
            switch (resultOption) {
                case Pinyin.RESULT_DEFAULT:
                    sb.append(pinyin.toString());
                    break;
                case Pinyin.RESULT_ASCII:
                    sb.append(pinyin.toStringAscii());
                    break;
                case Pinyin.RESULT_ASCII_NO_TONE:
                    sb.append(pinyin.toStringAsciiNoTone());
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        Assert.assertEquals(resultOption + "", Pinyin.getPinyinString(input, "", true, resultOption), sb.toString());
    }


    private void stringGetPinyinTest(String input) {
        Pinyin[] pinyins = Pinyin.getPinyin(input);
        for (int i = Pinyin.RESULT_DEFAULT; i <= Pinyin.RESULT_ASCII_NO_TONE; ++i) {
            stringGetPinyinTest(pinyins, input, i);
        }
    }

    @Test
    public void heteronymCountTest() {
        traverseDictionary(new AbsCharTester() {
            int heteronymCount = 0;
            int heteronymCountFromLength = 0;

            @Override
            void test(char c) {
                if (Pinyin.isHeteronym(c)) {
                    ++heteronymCount;
                }
                if (Pinyin.getPinyinAll(c).length > 1) {
                    ++heteronymCountFromLength;
                }
                Assert.assertEquals(heteronymCount, heteronymCountFromLength);
            }

            @Override
            void afterTraverse() {
                Assert.assertEquals(dictionary.heteronyms.length, heteronymCount);
                Assert.assertEquals(dictionary.heteronyms.length, heteronymCountFromLength);
            }
        });

    }


    @Test
    public void setGetExtraTest() {
        traverseDictionary(new AbsCharTester() {
            Pinyin copy = new Pinyin();
            Pinyin pinyin;

            @Override
            void test(char c) {
                pinyin = Pinyin.getPinyin(c);
                copy.unsafeSetInternal(pinyin.getInternal());
                for (int i = 0; i < 32; ++i) {
                    pinyin.setExtra(i);
                    Assert.assertEquals(i, pinyin.getExtra());
                    Assert.assertEquals(copy.getInitial(), pinyin.getInitial());
                    Assert.assertEquals(copy.getFinal(), pinyin.getFinal());
                    Assert.assertEquals(copy.getFinalAscii(), pinyin.getFinalAscii());
                    Assert.assertEquals(copy.getTone(), pinyin.getTone());
                }
            }
        });
    }

}
