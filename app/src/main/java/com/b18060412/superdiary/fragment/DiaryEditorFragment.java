package com.b18060412.superdiary.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.b18060412.superdiary.DiaryActivity;
import com.b18060412.superdiary.R;
import com.b18060412.superdiary.db.DiaryItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DiaryEditorFragment extends Fragment {
    private static final String TAG = "DiaryEditorFragment";
    private DiaryItem diaryItem;
    private EditText ET_editor;

    public DiaryEditorFragment() {}

    public String getContent(){
        return ET_editor.getText().toString();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_editor,container,false);//
        ET_editor = view.findViewById(R.id.ET_editor);
        DiaryActivity diaryActivity = (DiaryActivity) getActivity();
        diaryItem = diaryActivity.getDiaryItem();
        if (!new SimpleDateFormat("dd", Locale.CHINA).format(diaryItem.getDate()).equals(new SimpleDateFormat("dd", Locale.CHINA).format(Calendar.getInstance().getTimeInMillis()))){ET_editor.setHint("那天怎么样？");}
        ET_editor.setText(diaryItem.getContent());
        return view;
    }


}
