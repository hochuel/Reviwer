package com.srv.reviewer.service;

import com.srv.reviewer.vo.ReViewVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DataServiceImpl extends DataService{

    private String path = "https://happyworkshop.tistory.com";

    @Override
    public ArrayList<ReViewVO> getContentsList(ArrayList<ReViewVO> reViewerList) {
        String subPath = "/category/Enjoy?page="+getPage();
        try {
            Document doc = Jsoup.connect(path + subPath).get();
            Elements contents = doc.select("#dkContent");
            contents = contents.select("#mArticle");
            contents = contents.select("div.list_content");

            int i = 0;
            for(Element element : contents){
                Elements elements = element.getElementsByTag("a");
                String link = elements.attr("href");

                String name = element.getElementsByTag("strong").html();

                System.out.println(link + "::" + name);

                ReViewVO vo = new ReViewVO();
                vo.setTitle(name);
                String url = path + link;

                vo.setLinkUrl(getContetns(url));

                System.out.println("set vo.getLinkUrl() " +  vo.getLinkUrl());

                reViewerList.add(vo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return reViewerList;
    }

    public String getContetns(String url){

        String retUrl = "";
        try {
            Document doc = Jsoup.connect(url).get();

            Elements elements = doc.select("#mArticle");
            elements = elements.select("div.area_view");
            for(Element element : elements){

                //System.out.println(element.select("div.area_view").html());
                //System.out.println(element.getElementsByTag("a").attr("href"));
                retUrl = element.getElementsByTag("a").attr("href");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return retUrl;
    }
}
