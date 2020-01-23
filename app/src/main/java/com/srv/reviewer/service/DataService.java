package com.srv.reviewer.service;

import com.srv.reviewer.vo.ReViewVO;

import java.util.ArrayList;
import java.util.List;

public interface DataService {
    public ArrayList<ReViewVO> getContentsList(String path, ArrayList<ReViewVO> list);
}
