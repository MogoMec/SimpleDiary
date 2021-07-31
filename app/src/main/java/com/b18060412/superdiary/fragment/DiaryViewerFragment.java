package com.b18060412.superdiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.b18060412.superdiary.DiaryActivity;
import com.b18060412.superdiary.R;
import com.b18060412.superdiary.db.DiaryItem;
import com.b18060412.superdiary.fragment.DiaryEditorFragment;

public class DiaryViewerFragment extends Fragment {
    private TextView TV_viewer;
    private DiaryItem diaryItem;

    public DiaryViewerFragment() {}
    public void setContent(Fragment fragment){
        DiaryEditorFragment mfragment = (DiaryEditorFragment)fragment;
        TV_viewer.setText(mfragment.getContent());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_viewer,container,false);//
        TV_viewer = view.findViewById(R.id.TV_viewer);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DiaryActivity diaryActivity = (DiaryActivity) getActivity();
        diaryItem = diaryActivity.getDiaryItem();
        TV_viewer.setText(diaryItem.getContent());
    }
}
