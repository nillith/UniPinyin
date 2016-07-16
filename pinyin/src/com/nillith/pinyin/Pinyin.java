package com.nillith.pinyin;


public class Pinyin {
    private static final Dictionary dict = Dictionary.getInstance();


    public static Pinyin parsePinyin(String s) {
        return new Pinyin((char) Meta.indexOf(s));
    }

    public static Pinyin getPinyin(char c) {
        return new Pinyin(dict.get(c));
    }

    public static Pinyin[] getPinyin(String str) {
        int num = str.length();
        Pinyin[] result = new Pinyin[num];
        for (int i = 0; i < num; ++i) {
            result[i] = getPinyin(str.charAt(i));
        }
        return result;
    }

    public static String getPinyinString(char c) {
        return getPinyin(c).toString();
    }

    public static String getPinyinStringAscii(char c) {
        return getPinyin(c).toStringAscii();
    }

    public static String getPinyinStringAsciiNoTone(char c) {
        return getPinyin(c).toStringAsciiNoTone();
    }


    public static String getPinyinString(String str) {
        return getPinyinString(str, DELIMITER_DEFAULT, IGNORE_NOT_FOUND);
    }

    public static String getPinyinStringAscii(String str) {
        return getPinyinStringAscii(str, DELIMITER_DEFAULT, IGNORE_NOT_FOUND);
    }

    public static String getPinyinStringAsciiNoTone(String str) {
        return getPinyinStringAsciiNoTone(str, DELIMITER_DEFAULT, IGNORE_NOT_FOUND);
    }


    static final int PINYIN_LENGTH_AVG = 4;

    static final String DELIMITER_DEFAULT = " ";

    static final boolean IGNORE_NOT_FOUND = false;
    static final int RESULT_DEFAULT = 0;
    static final int RESULT_ASCII = 1;
    static final int RESULT_ASCII_NO_TONE = 2;

    static String getPinyinString(String str, String delimiter, boolean ignoreNotFound, final int resultOption) {
        if (null == delimiter) {
            delimiter = "";
        }
        Pinyin pinyin = new Pinyin();
        StringBuilder sb = new StringBuilder((PINYIN_LENGTH_AVG + delimiter.length()) * str.length());
        char c;
        for (int i = 0, end = str.length(); i < end; ++i) {
            c = str.charAt(i);
            pinyin.data = dict.get(c);
            if (0 == pinyin.data) {
                if (!ignoreNotFound) {
                    sb.append(c);
                    sb.append(delimiter);
                }
            } else {
                switch (resultOption) {
                    case RESULT_DEFAULT:
                        sb.append(pinyin.toString());
                        break;
                    case RESULT_ASCII:
                        sb.append(pinyin.toStringAscii());
                        break;
                    case RESULT_ASCII_NO_TONE:
                        sb.append(pinyin.toStringAsciiNoTone());
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                sb.append(delimiter);
            }
        }
        return sb.toString().substring(0, sb.length() - delimiter.length());
    }

    public static String getPinyinString(String str, String delimiter, boolean ignoreNotFound) {
        return getPinyinString(str, delimiter, ignoreNotFound, RESULT_DEFAULT);
    }

    public static String getPinyinStringAscii(String str, String delimiter, boolean ignoreNotFound) {
        return getPinyinString(str, delimiter, ignoreNotFound, RESULT_ASCII);
    }

    public static String getPinyinStringAsciiNoTone(String str, String delimiter, boolean ignoreNotFound) {
        return getPinyinString(str, delimiter, ignoreNotFound, RESULT_ASCII_NO_TONE);
    }

    public static Pinyin[] getPinyinAll(char c) {
        Pinyin pinyin = getPinyin(c);
        if (pinyin.isMarkedAsHeteronym()) {
            char[] extras = dict.getExtras(c);
            Pinyin[] pinyins = new Pinyin[extras.length];
            pinyins[0] = pinyin;
            for (int i = 1; i < pinyins.length; ++i) {
                pinyins[i] = new Pinyin(extras[i]);
            }
            return pinyins;
        } else {
            return new Pinyin[]{pinyin};
        }
    }

    public String getInitial() {
        return getMeta().getInitial();
    }

    public String getFinal() {
        return getMeta().getFinal();
    }

    public String getFinalAscii() {
        return getMeta().getFinalAscii();
    }

    public int getTone() {
        return getMeta().getTone();
    }

    @Override
    public String toString() {
        return getMeta().toString();
    }

    public String toStringAscii() {
        return getMeta().toStringAscii();
    }

    public String toStringAsciiNoTone() {
        return getMeta().toStringAsciiNoTone();
    }

    public Pinyin() {

    }

    Pinyin(char data) {

        this.data = data;
    }

    public void unsafeSetInternal(char data) {
        this.data = data;
    }

    public char getInternal() {
        return data;
    }

    private Meta getMeta() {
        return Meta.ALL[getMetaIndex()];
    }

    char data;


    private static final char IS_HETERONYM = 1;

    public static boolean isHeteronym(char c) {
        return getPinyin(c).isMarkedAsHeteronym();
    }

    boolean isMarkedAsHeteronym() {
        return getExtra() == IS_HETERONYM;
    }

    void markAsHeteronym() {
        setExtra(IS_HETERONYM);
    }

    private static final int IDX_BITS = 11;
    private static final char IDX_MASK = 0x7ff;

    int getMetaIndex() {
        int result = data & IDX_MASK;
        return result;
    }


    int getExtra() {
        return data >>> IDX_BITS;
    }


    void setExtra(int extra) {
        data = (char) (getMetaIndex() | (extra << IDX_BITS));
    }
}
