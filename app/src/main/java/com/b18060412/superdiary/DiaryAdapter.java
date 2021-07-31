package com.b18060412.superdiary;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b18060412.superdiary.db.DiaryItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private List<DiaryItem> DiaryList;
    private MainActivity context;

    private static final int REQUEST_CODE_EDIT = 1;

    public DiaryAdapter(List<DiaryItem> diaryList, Context context) {
        DiaryList = diaryList;
        this.context = (MainActivity) context;
    }

    public DiaryAdapter(List<DiaryItem> diaryList) {
        DiaryList = diaryList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View diaryView;
        ImageView IV_image;
        TextView TV_date_day,TV_date_year,TV_mood,TV_content,TV_weather,TV_location;

        public ViewHolder(View view){
            super(view);
            diaryView = view;
            TV_date_day = view.findViewById(R.id.TV_date_day);
            TV_date_year = view.findViewById(R.id.TV_date_year);
            TV_mood = view.findViewById(R.id.TV_mood);
            TV_content = view.findViewById(R.id.TV_content);
            TV_weather = view.findViewById(R.id.TV_weather);
            TV_location = view.findViewById(R.id.TV_location);

        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.diaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                DiaryItem diaryItem = DiaryList.get(position);
                Log.e("DiaryAdapter", "onClick: "+position );
                Intent intent = new Intent(context,DiaryActivity.class);
                intent.putExtra("DiaryId",diaryItem.getId());
                intent.putExtra("SelectedPosition",position);
                context.startActivityForResult(intent,REQUEST_CODE_EDIT);
            }
        });
        holder.diaryView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                int position = holder.getAdapterPosition();
                DiaryItem diaryItem = DiaryList.get(position);
                context.setSelectedDiaryId(diaryItem.getId(),position);//调用MainActivity方法，传出选中日记ID以及列表位置
                menu.add(0,0,0,"编辑");
                menu.add(0,1,0,"删除");
                Log.e("item", "onCreateContextMenu: "+diaryItem.getId() );
            }
        });
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryItem diaryItem = DiaryList.get(position);
        holder.TV_date_day.setText(getDay(diaryItem.getDate()));
        holder.TV_date_year.setText(getYearMonth(diaryItem.getDate()));
        holder.TV_mood.setText(diaryItem.getMood());
        holder.TV_content.setText(diaryItem.getContent());
        holder.TV_weather.setText(diaryItem.getWeather());
        holder.TV_location.setText(diaryItem.getLocation());
    }

    @Override
    public int getItemCount() {
        return DiaryList.size();
    }

    public static String getDay(long milliseconds) {
        return new SimpleDateFormat("dd", Locale.CHINA).format(milliseconds);
    }

    public static String getYearMonth(long milliseconds) {
        return new SimpleDateFormat("MM月/yyyy年\nEEEE", Locale.CHINA).format(milliseconds);
    }


}
