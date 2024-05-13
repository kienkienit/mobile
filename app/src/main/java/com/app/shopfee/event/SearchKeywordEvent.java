package com.app.shopfee.event;

public class SearchKeywordEvent {

    private String keyword;

    public SearchKeywordEvent(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
