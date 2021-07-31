package com.b18060412.superdiary.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.b18060412.superdiary.DiaryActivity;
import com.b18060412.superdiary.db.DBManager;
import com.b18060412.superdiary.db.DiaryItem;

public class DiarySaveDialog extends CommonDialog{


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.TV_hint.setText("确定保存日记吗？");
    }


    @Override
    protected void clickOnOkButton() {
        DiaryActivity diaryActivity = (DiaryActivity)getActivity();
        diaryActivity.updateDiaryContent();//日记修改后
        DiaryItem diaryItem;
        int position = diaryActivity.getIntent().getIntExtra("SelectedPosition",-1);
        if (position != -1){
            DBManager.getInstance().update(diaryActivity.getDiaryItem());
            diaryItem = diaryActivity.getDiaryItem();
            Log.e("save", "clickOnOkButton: "+ diaryActivity.getDiaryItem().getDate());
        }else{
            DBManager.getInstance().insert(diaryActivity.getDiaryItem());
            diaryItem = DBManager.getInstance().getByDate(diaryActivity.getDiaryItem().getDate());
        }
        Intent intent = new Intent();
        intent.putExtra("diaryitem",diaryItem);
        intent.putExtra("SelectedPosition",position);
        diaryActivity.setResult(Activity.RESULT_OK,intent);
        dismiss();
        diaryActivity.finish();
    }

    @Override
    protected void clickOnCancelButton() {
        dismiss();
        DiaryActivity diaryActivity = (DiaryActivity)getActivity();
        diaryActivity.finish();
    }
}
