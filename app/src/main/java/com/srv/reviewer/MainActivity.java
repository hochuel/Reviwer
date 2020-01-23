package com.srv.reviewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.srv.reviewer.adapter.ReviwerAdater;
import com.srv.reviewer.service.DataService;
import com.srv.reviewer.service.DataServiceImpl;
import com.srv.reviewer.vo.ReViewVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ReViewVO> reViewerList;
    private ReviwerAdater reviwerAdater;

    private SwipyRefreshLayout swipyRefreshLayout;

    private int page = 1;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reViewerList = new ArrayList<ReViewVO>();

        Object[] obj = null;
        UrlParseDataTask task = new UrlParseDataTask(this);
        task.execute(obj);

        ListView listView = (ListView)findViewById(R.id.listView);

        reviwerAdater = new ReviwerAdater(this, reViewerList);
        listView.setAdapter(reviwerAdater);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReViewVO reViewVO = reViewerList.get(position);
                System.out.println("reViewVO.getLinkUrl() ::" + reViewVO.getLinkUrl());

                Intent intent = new Intent(getApplicationContext(), movieActivity.class);
                intent.putExtra("moveUrl", reViewVO.getLinkUrl());
                startActivity(intent);
            }
        });

        swipyRefreshLayout = findViewById(R.id.swipyrefreshlayout);

        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                System.out.println("###################Refresh....." + direction);

                if(direction == SwipyRefreshLayoutDirection.BOTTOM){
                    page ++;

                    GetDataThread getDataThread = new GetDataThread();
                    getDataThread.setSwipyRefreshLayout(swipyRefreshLayout);
                    getDataThread.start();
                }else{
                    swipyRefreshLayout.setRefreshing(false);
                }

            }
        });

    }


    private class GetDataThread extends Thread{

        private SwipyRefreshLayout swipyRefreshLayout;

        public SwipyRefreshLayout getSwipyRefreshLayout() {
            return swipyRefreshLayout;
        }

        public void setSwipyRefreshLayout(SwipyRefreshLayout swipyRefreshLayout) {
            this.swipyRefreshLayout = swipyRefreshLayout;
        }

        @Override
        public void run() {
            String path = "https://hotissueone.tistory.com/category/%EB%8B%A4%EC%8B%9C%EB%B3%B4%EA%B8%B0?page="+page;
            DataService dataService = new DataServiceImpl();
            dataService.getContentsList(path, reViewerList);


            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    reviwerAdater.notifyDataSetChanged();
                }
            });

            swipyRefreshLayout.setRefreshing(false);
        }
    }


    private class UrlParseDataTask extends AsyncTask {
        ProgressDialog progressDialog;
        private Context mContext;


        public UrlParseDataTask(Context context) {
            mContext = context;
        }

        @Override
        protected List doInBackground(Object[] objects) {

            /*
            try {
                Document doc = Jsoup.connect("https://hotissueone.tistory.com/category/%EB%8B%A4%EC%8B%9C%EB%B3%B4%EA%B8%B0").get();
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
                    String url = "https://hotissueone.tistory.com" + link;

                    vo.setLinkUrl(getContetns(url));

                    System.out.println("set vo.getLinkUrl() " +  vo.getLinkUrl());

                    reViewerList.add(vo);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            */

            String path = "https://hotissueone.tistory.com/category/%EB%8B%A4%EC%8B%9C%EB%B3%B4%EA%B8%B0?page="+page;
            DataService dataService = new DataServiceImpl();
            dataService.getContentsList(path, reViewerList);

            return reViewerList;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("접속 시작");
            progressDialog.setCancelable(false);
            //progressDialog.setProgress( 0 );
            progressDialog.setMax(15);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Object object) {
            //onPostExecute() 함수는 doInBackground() 함수가 종료되면 실행됨
            progressDialog.dismiss();
            reviwerAdater.notifyDataSetChanged();
            Toast.makeText(mContext, "작업 완료", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            int progress = (Integer) values[0];
            progressDialog.setMessage("데이터 수신 중...");
            progressDialog.setProgress(progress);
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
}
