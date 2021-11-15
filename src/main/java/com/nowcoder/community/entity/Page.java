package com.nowcoder.community.entity;

import org.springframework.context.annotation.Bean;

/**
 * 封装分页相关的条件
 */
public class Page {

    //浏览器向服务器传入的数据
    private int current = 1;//当前页码
    private int limit = 10;//显示页数的上限

    //服务器查询后，向浏览器返回的数据
    private int rows;//数据总数(用于计算总页数)
    private String path;//查询路径(用于复用分页链接)

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset(){
        //current * limit - limit
        int offset = (current - 1) * limit;
        return offset;
    }

    /**
     * 获取总页码数
     * @return
     */
    public int getTotal(){
        //rows / limit [+1]
        int total;
        if(rows % limit == 0){
            total = rows / limit;
        }else {
            total = rows / limit + 1;
        }
        return total;
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1:from;
    }

    /**
     * 获取结束页码
     * @return
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total:to;
    }

    public Page() {
    }

    public Page(int rows, String path) {
        this.rows = rows;
        this.path = path;
    }

    public Page(int current, int limit, int rows, String path) {
        this.current = current;
        this.limit = limit;
        this.rows = rows;
        this.path = path;
    }
}
