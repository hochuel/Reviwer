package com.srv.reviewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.*;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.srv.reviewer.adapter.ReviwerAdater;
import com.srv.reviewer.service.DataService;
import com.srv.reviewer.service.DataServiceImpl;
import com.srv.reviewer.vo.ReViewVO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ReViewVO> reViewerList;
    private ReviwerAdater reviwerAdater;
    private SwipyRefreshLayout swipyRefreshLayout;
    private int page = 1;
    Handler mHandler = new Handler();
    private CustomDialog customDialog;
    private AdView mAdView;

    private DataService dataService;


    private InterstitialAd mInterstitialAd;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu01:
                /*
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("INFO").setMessage("Email : hochuel@gmail.com");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        //Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                */

                ProgrammerInfoDialog programmerInfoDialog = new ProgrammerInfoDialog(this, "", "hochuel@gmail.com");
                programmerInfoDialog.setCancelable(true);
                programmerInfoDialog.getWindow().setGravity(Gravity.CENTER);
                programmerInfoDialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Log.d("@@MainActivity msg1","onBackPressed");
        //Toast.makeText(getApplicationContext(),"onBackPressed",Toast.LENGTH_SHORT).show();

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // 사용자가 광고를 닫으면 뒤로가기 이벤트를 발생시킨다.
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }


        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded(){
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("@@ADMOB...$$", "The interstitial wasn't loaded yet.");


                }
            }
        });

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataService = new DataServiceImpl();

        /*광고달기 시작*/
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id_for_test));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
/*
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded(){
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("@@ADMOB...$$", "The interstitial wasn't loaded yet.");
                }
            }
        });
*/
        /*광고달기 끝...*/

        customDialog = new CustomDialog(this);
        reViewerList = new ArrayList<ReViewVO>();

        Object[] obj = null;
        UrlParseDataTask task = new UrlParseDataTask(this, dataService);
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

                    dataService.setPage(page);
                    getDataThread.setDataService(dataService);
                    getDataThread.start();
                }else{
                    swipyRefreshLayout.setRefreshing(false);
                }

            }
        });

    }


    private class GetDataThread extends Thread{

        private SwipyRefreshLayout swipyRefreshLayout;
        private DataService dataService;

        public DataService getDataService() {
            return dataService;
        }

        public void setDataService(DataService dataService) {
            this.dataService = dataService;
        }

        public SwipyRefreshLayout getSwipyRefreshLayout() {
            return swipyRefreshLayout;
        }

        public void setSwipyRefreshLayout(SwipyRefreshLayout swipyRefreshLayout) {
            this.swipyRefreshLayout = swipyRefreshLayout;
        }

        @Override
        public void run() {
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
        private DataService dataService;

        public UrlParseDataTask(Context context, DataService dataService) {
            mContext = context;
            this.dataService = dataService;
        }

        @Override
        protected List doInBackground(Object[] objects) {
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
