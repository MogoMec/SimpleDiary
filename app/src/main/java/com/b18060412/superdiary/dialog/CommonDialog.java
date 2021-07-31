package com.b18060412.superdiary.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.b18060412.superdiary.R;

public abstract class CommonDialog extends DialogFragment implements View.OnClickListener { //抽象类，为提示对话框提供统一视觉风格以及按钮点击监听

    protected Button BT_ok,BT_cancel;
    protected TextView TV_hint;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_common,container);
        BT_cancel = view.findViewById(R.id.BT_cancel);
        BT_ok = view.findViewById(R.id.BT_ok);
        TV_hint = view.findViewById(R.id.TV_hint);

        BT_ok.setOnClickListener(this);
        BT_cancel.setOnClickListener(this);
        return view;
    }

    protected  abstract void clickOnOkButton();
    protected  abstract void clickOnCancelButton();

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BT_cancel:
                clickOnCancelButton();
                break;
            case R.id.BT_ok:
                clickOnOkButton();
                break;
        }
    }
}
