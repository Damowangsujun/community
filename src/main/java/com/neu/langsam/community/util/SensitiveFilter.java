package com.neu.langsam.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component//交给容器管理
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换符
    private static String REPLACEMENT = "***";

    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        try(
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                ) {
            String keyword = null;
            while((keyword = bufferedReader.readLine()) != null) {
                this.addKeyword(keyword);
            }
        }catch (IOException e) {
            logger.error("加载敏感词失败：" + e.getMessage());
        }
    }
    //将敏感词添加到前缀树当中
    private void addKeyword(String keyword) {
        TrieNode node = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = node.getNext(c);
            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                node.insert(c, subNode);
            }
            //指向子节点，进入下一轮循环
            node = subNode;

            //设置结束标识
            if (i == keyword.length() - 1) {
                node.setEnd(true);
            }
        }
    }
    //判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80-0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    /**
     * 过滤敏感词
     *
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        //指针1
        TrieNode tempNode = root;

        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            //跳过符号
            if (isSymbol(c)) {
                //若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if (tempNode == root) {
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;

            }

            //检查下级节点
            tempNode = tempNode.getNext(c);
            if (tempNode == null) {
                //以begin开头的字符不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = root;
            } else if (tempNode.isEnd()) {
                //发现敏感词，将begin-position字符串替换掉
                sb.append(REPLACEMENT);
                begin = ++position;
                //重新指向根节点
                tempNode = root;
            } else {
                //检查下一个字符
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    private class TrieNode {
        /**
         * 前缀树这样一种结构 -- 包括isEnd,next
         */
        private boolean isEnd = false;
        private Map<Character, TrieNode> next = new HashMap<>();

        public boolean isEnd() {
            return isEnd;
        }
        public void setEnd(boolean end) {
            isEnd = end;
        }

        //添加子节点方法
        public void insert(Character key, TrieNode value) {
            next.put(key, value);
        }

        public TrieNode getNext(Character key) {
            return next.get(key);
        }
    }
}
