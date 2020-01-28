package com.srv.reviewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        customDialog = new CustomDialog(this);

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
            DataService dataService = new DataServiceImpl();
            dataService.setPage(page);
            dataService.getContentsList(reViewerList);


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
        private Context mContext;
        private ProgressBar progressBar;

        public UrlParseDataTask(Context context) {
            mContext = context;
        }

        @Override
        protected List doInBackground(Object[] objects) {

            DataService dataService = new DataServiceImpl();
            dataService.getContentsList(reViewerList);

            return reViewerList;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customDialog.show();
        }

        @Override
        protected void onPostExecute(Object object) {
            //onPostExecute() 함수는 doInBackground() 함수가 종료되면 실행됨
            customDialog.dismiss();
            reviwerAdater.notifyDataSetChanged();
            Toast.makeText(mContext, "작업 완료", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            int progress = (Integer) values[0];
        }

    }
}
