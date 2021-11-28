package com.nowcoder.community.util;

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

@Component
public class SensitiveFilter {

    public static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //初始化根结点
    private TrieNode rootNode = new TrieNode();

    //初始化前缀树
    @PostConstruct
    private void init(){
        //读取敏感词
        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while ((keyword = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
           logger.error("加载过滤敏感词文件失败："+e.getMessage());
        }
    }

    //添加到前缀树
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0;i < keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //移到下一层，继续下一轮循环添加
            tempNode = subNode;

            //设置结束标识
            if(i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()){
            char c = text.charAt(position);
            //跳过特殊字符
            if(isSymbol(c)){
                //如果指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头还是重点，指针3都向下走一步
                position++;
                continue;
            }
            //检查下级结点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                //从begin开头的字符不是敏感词
                sb.append(text.charAt(begin));
                begin++;
                position = begin;
                //重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                //begin~position的字符是敏感词
                sb.append(REPLACEMENT);
                position++;
                begin = position;
                tempNode = rootNode;
            }else{
                position++;
            }

        }
        //将最后一批计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    //判断是否为特殊字符
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF是东亚文字，不是特殊字符
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //内部类：定义前缀树的数据结构
    private class TrieNode{

        //关键词结束标志
        private boolean isKeywordEnd = false;

        //子结点(键值对的键为子节点存储的字符，值为子结点)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子结点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        //获取子结点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
