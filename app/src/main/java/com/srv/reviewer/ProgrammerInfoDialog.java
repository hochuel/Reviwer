package com.srv.reviewer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class ProgrammerInfoDialog extends Dialog {

    private TextView mTitleView;
    private TextView mContentView;
    private Button mLeftButton;
    private Button mRightButton;
    private String mTitle;
    private String mContent;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;


    public ProgrammerInfoDialog(Context context, String title, String content) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent=content;
        this.mLeftClickListener = leftListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.programmer_layout);


        mTitleView = (TextView) findViewById(R.id.dialog_title);
        mContentView = (TextView) findViewById(R.id.dialog_text);
        mLeftButton = (Button) findViewById(R.id.dialog_btn);
        //mRightButton = (Button) findViewById(R.id.dialog_btn2);

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
        mContentView.setText(mContent);

        // 클릭 이벤트 셋팅

        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }
    }


    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Toast.makeText(getContext(), "버튼을 클릭하였습니다.", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    };

}
