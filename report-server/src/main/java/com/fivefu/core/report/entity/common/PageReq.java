package com.fivefu.core.report.entity.common;

/**
 * 分页请求父类
 */
public class PageReq {
    private Integer page;

    private Integer limit;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
