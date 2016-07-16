package com.nillith.demo.pinyin;

import com.nillith.pinyin.Pinyin;

public class Main {

    public static void main(String[] str) {

        System.out.println(Pinyin.getPinyinString("你好，世界！"));
        System.out.println(Pinyin.getPinyinStringAscii("你好，世界！"));
        System.out.println(Pinyin.getPinyinStringAsciiNoTone("你好，世界！"));
        System.out.println(Pinyin.getPinyinString("你好，世界！", "-", true));

        Pinyin hao = Pinyin.getPinyin('好');
        System.out.println(hao.getInitial());
        System.out.println(hao.getFinal());
        System.out.println(hao.getFinalAscii());
        System.out.println(hao.getTone());

        System.out.println(hao.toString());
        System.out.println(hao.toStringAscii());
        System.out.println(hao.toStringAsciiNoTone());

        System.out.print(Pinyin.isHeteronym('好'));
        Pinyin[] all = Pinyin.getPinyinAll('好');
        for (Pinyin p : all) {
            System.out.println(p);
        }

        Pinyin[] hw = Pinyin.getPinyin("你好，世界！");
        for (Pinyin p: hw){
            System.out.println(p);
        }
    }
}
