UniPinyin
============
Java 汉语拼音查询工具。支持CJK基本字符集及扩充区A的中文汉字，共26679个，其中多音字6769个。

[ ![下载](https://api.bintray.com/packages/nillith/maven/UniPinyin/images/download.svg) ](https://bintray.com/nillith/maven/UniPinyin/_latestVersion)

#用法
##1 下载 [jar 包](https://bintray.com/nillith/maven/download_file?file_path=com%2Fnillith%2Funipinyin%2F1.0.0%2Funipinyin-1.0.0.jar)或添加gradle依赖：

```groovy
compile 'com.nillith:unipinyin:1.0.0'
```

##2 使用

```java
// 返回字符串
Pinyin.getPinyinString('你'); // nǐ 
Pinyin.getPinyinString("你好，世界！"); // nǐ hǎo ， shì jiè ！

// 返回Pinyin对象
Pinyin hao = Pinyin.getPinyin('好');
Pinyin[] hw = Pinyin.getPinyin("你好，世界！");

// Pinyin对象的使用
hao.getInitial(); // h 声母
hao.getFinal(); // ǎo 韵母 
hao.getFinalAscii(); // ao 韵母的ascii形式
hao.getTone(); // 3 声调
hao.toString(); // hǎo
hao.toStringAscii(); // hao3
hao.toStringAsciiNoTone(); // hao


// 其他字符串方法
Pinyin.getPinyinStringAscii("你好，世界！"); // ni3 hao3 ， shi4 jie4 ！
Pinyin.getPinyinStringAsciiNoTone("你好，世界！"); // ni hao ， shi jie ！
Pinyin.getPinyinString("你好，世界！", "-"/*自定义分隔符*/, true/*忽略无查询结果的字符*/); // nǐ-hǎo-shì-jiè


Pinyin.isHeteronym('好'); // true  判断是否是多音字
Pinyin[] all = Pinyin.getPinyinAll('好'); // 获取多音字所有的拼音对象
```
## License

Nillith, 2016. Licensed under an [Apache-2](LICENSE.txt) license.
