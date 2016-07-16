package com.nillith.pinyin;


import org.junit.Assert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;


public class DictCompiler {

    private static final String DICTIONARY_BIN = "pinyin/resources/dictionary.bin";
        private static final String DICTIONARY_SRC = "pinyin.txt";


    public static void main(String[] args) throws IOException {
//        turnOnLog();
        Dictionary dictionary = compile();
        generateDictionaryBinaryFile(dictionary);
        System.out.println(String.format("Empty records: %d.",dictionary.emptyCount));
        System.out.println(String.format("Total records: %d.",dictionary.major.length));
        System.out.println(String.format("Heteronyms: %d.",dictionary.heteronyms.length));
    }



    interface LineParser {
        void parse(int lineNumber, int key, String[] values);
    }


    static final String EMPTY_PINYIN_PLACEHOLDER = "----";
    private static final String COMMENT_INDICATOR = "//";
    private static final String KV_SEPARATOR_PATTERN = "(\\s*:\\s*)";
    private static final String VALUE_SEPARATOR_PATTERN = "\\s*,\\s*";
    private static final String KV_SEPARATOR = ":";
    private static final String VALUE_SEPARATOR = ",";
    private static final String COMMENT_SEPARATOR = " " + COMMENT_INDICATOR + " ";


    private static boolean printLog = false;

    public static void turnOnLog() {
        printLog = true;
    }

    public static void turnOffLog() {
        printLog = true;
    }

    public static String toString(Dictionary dictionary) {
        if (null == dictionary) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Pinyin pinyin = new Pinyin();
        String pyStr;
        char c;
        for (char i = 0, end = (char) dictionary.major.length; i < end; ++i) {
            c = dictionary.toCharacter(i);
            sb.append(Integer.toHexString(c));
            sb.append(KV_SEPARATOR);
            pinyin.data = dictionary.major[i];
            pyStr = pinyin.toStringAscii();
            if (pyStr.equals("")) {
                if (pinyin.isMarkedAsHeteronym()) {
                    System.out.println(c + ":" + Integer.toHexString(c) + ":" + ((int) pinyin.data));
                }
                Assert.assertTrue(pinyin.toString(), !pinyin.isMarkedAsHeteronym());
                sb.append(EMPTY_PINYIN_PLACEHOLDER);
            } else {
                sb.append(pyStr);
                if (pinyin.isMarkedAsHeteronym()) {
                    char[] datas = dictionary.getExtras(c);
                    for (int j = 1; j < datas.length; ++j) {
                        pinyin.data = datas[j];
                        pyStr = pinyin.toStringAscii();

                        Assert.assertTrue(c + ":" + Integer.toHexString(c) + ":" + ((int) pinyin.data), !pyStr.equals(""));
                        sb.append(VALUE_SEPARATOR);
                        sb.append(pyStr);
                    }
                }
            }
            sb.append(COMMENT_SEPARATOR);
            sb.append(c);
            sb.append('\n');
        }
        return sb.toString();
    }


    public static ArrayList<Character> findAllEmpty(Dictionary dictionary) {
        ArrayList<Character> result = new ArrayList<>();
        Pinyin pinyin = new Pinyin();
        for (int i = 0; i < dictionary.major.length; ++i) {
            pinyin.data = dictionary.major[i];
            if (pinyin.toString().equals("")) {
                result.add(dictionary.toCharacter(i));
            }
        }
        return result;
    }


    private static void logParse(int linenumber, String tag, String msg) {
        if (printLog) {
            System.out.println(String.format("Line %d: %s: %s", linenumber, tag, msg));
        }
    }

    public static void parsePinyinSrc(LineParser parser) {
        Scanner scanner = null;
        String[] kvSplit;
        String line;
        String code;
        String comment = "";
        int lineNumber = 0;
        int commentStart;
        int c = 0;
        try {
            scanner = new Scanner(DictCompiler.class.getClassLoader().getResourceAsStream(DICTIONARY_SRC), "utf8");
            while (scanner.hasNextLine()) {
                ++lineNumber;
                line = scanner.nextLine().trim();

                if (line.startsWith(COMMENT_INDICATOR)) {
                    logParse(lineNumber, "comment", line);
                    continue;
                }
                commentStart = line.indexOf(COMMENT_INDICATOR);

                if (-1 != commentStart) {
                    code = line.substring(0, commentStart).trim();
                    comment = line.substring(commentStart + COMMENT_INDICATOR.length()).trim();
                } else {
                    code = line;
                }

                kvSplit = code.split(KV_SEPARATOR_PATTERN);
                Assert.assertEquals(String.format("Invalid KV-pair: %s!", code), 2, kvSplit.length);

                logParse(lineNumber, "key", kvSplit[0]);

                logParse(lineNumber, "value", kvSplit[1]);

                if (-1 != commentStart) {
                    logParse(lineNumber, "comment", comment);
                }

                c = Integer.parseInt(kvSplit[0], 16);
                parser.parse(lineNumber, c, kvSplit[1].split(VALUE_SEPARATOR_PATTERN));

                if (printLog) {
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.out.println(String.format("Line: %d, Char: %s", lineNumber, Integer.toHexString(c)));
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != scanner) {
                scanner.close();
            }
        }
    }


    public static void generateDictionaryBinaryFile(Dictionary dictionary) {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new FileOutputStream(DICTIONARY_BIN));
            os.writeObject(dictionary);
            System.out.println("File dictionary.bin generated!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Dictionary compile() {
        DictionaryAssembler dictionaryAssembler = new DictionaryAssembler();
        dictionaryAssembler.printLog = printLog;
        parsePinyinSrc(dictionaryAssembler);
        return dictionaryAssembler.assemble();
    }
}
