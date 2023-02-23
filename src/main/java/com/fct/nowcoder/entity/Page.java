package com.fct.nowcoder.entity;

/**
 * 封装分页相关信息
 */
public class Page {

    //当前页码
    private Integer current = 1;
    //显示上限
    private Integer limit = 10;
    //数据总数
    private Integer rows;
    //查询路径
    private String path;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        if(limit >= 1 && limit <= 100)
        this.limit = limit;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
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
    public Integer getOffset(){
        // current* limit -limit
        return (current - 1)* limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public Integer getTotal(){
        if(rows % limit == 0){
            return rows/limit;
        }else{
            return rows/limit +1;
        }
    }

    /**
     * 获取终止页
     * @return
     */
    public Integer getFrom(){
        Integer from = current -2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取终止页
     * @return
     */
    public Integer getTo(){
        Integer to = current + 2;
        Integer total = this.getTotal();
        return to > total ? total : to;

    }
}
