package com.srv.reviewer.service;

import com.srv.reviewer.vo.ReViewVO;

import java.util.ArrayList;
import java.util.List;

public abstract class DataService {

    private int page = 0;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public abstract ArrayList<ReViewVO> getContentsList(ArrayList<ReViewVO> list);
}
