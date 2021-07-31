package com.b18060412.superdiary.dialog;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.b18060412.superdiary.MainActivity;
import com.b18060412.superdiary.db.DBManager;

public class DiaryDeleteDialog extends CommonDialog {

    private String id;

    public DiaryDeleteDialog(String id) {
        this.id = id;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.TV_hint.setText("你确定要删除该日记吗？");
    }


    @Override
    protected void clickOnOkButton() {
        DBManager.getInstance().deleteById(id);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.removeDiaryItem();
        dismiss();
    }

    @Override
    protected void clickOnCancelButton() {
        dismiss();
    }
}
