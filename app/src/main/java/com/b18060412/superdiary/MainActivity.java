package com.b18060412.superdiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.b18060412.superdiary.db.DBManager;
import com.b18060412.superdiary.db.DiaryItem;
import com.b18060412.superdiary.dialog.DiaryDeleteDialog;
import com.b18060412.superdiary.dialog.ProfileDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.haibin.calendarview.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,CalendarView.OnCalendarSelectListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private LinearLayout LL_profile;
    private FloatingActionButton FAB_edit;
    private NavigationView NV_profile;
    private View navi_header;
    private TextView TV_profile_name;
    private TextView TV_navi_profile_name;
    private RecyclerView RV_diary;
    private List<DiaryItem> diaryList = new ArrayList<>();
    private String selectedDiaryId;
    private int selectedItemPosition;
    private CalendarView CV_calendar;
    private ImageView IV_calendar;
    private Map<String, com.haibin.calendarview.Calendar> map = new HashMap<>();
    private Boolean isSelectedDateNull = true;
    DiaryAdapter diaryAdapter;
    private Menu navi_menu;
    private MenuItem navi_textsum,navi_diarycount;



    private static final int REQUEST_CODE_ADD = 0;
    private static final int REQUEST_CODE_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate: " );
        //抽屉菜单
        drawerLayout = findViewById(R.id.DL_menu);

        //用户信息
        LL_profile = findViewById(R.id.LL_profile);
        LL_profile.setOnClickListener(this);

        //悬浮按钮
        FAB_edit = findViewById(R.id.FAB_edit);
        FAB_edit.setOnClickListener(this);

        //抽屉导航头图
        NV_profile = findViewById(R.id.NV_profile);
        NV_profile.inflateHeaderView(R.layout.navi_header);
        navi_header = NV_profile.getHeaderView(0);
        TV_navi_profile_name = navi_header.findViewById(R.id.TV_navi_profile_name);
        navi_header.setOnClickListener(this);
        TV_profile_name = findViewById(R.id.TV_profile_name);

        //日历
        CV_calendar = findViewById(R.id.CV_calendar);
        IV_calendar = findViewById(R.id.IV_calendar);
        IV_calendar.setOnClickListener(this);


        //日记RecyclerView
        RV_diary = findViewById(R.id.RV_diary);
        registerForContextMenu(RV_diary);//注册长按菜单


        navi_menu = NV_profile.getMenu();
        navi_textsum = navi_menu.findItem(R.id.navi_textsum);
        navi_diarycount = navi_menu.findItem(R.id.navi_diarycount);





        DBManager dbManager = DBManager.getInstance();
        dbManager.open(this);
        initProfile();//用户信息加载
        initExample();//示例日记加载
        loadDiary();//读取数据库日历记录
        initCalendar();//初始化日历标记

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RV_diary.setLayoutManager(linearLayoutManager);
        diaryAdapter = new DiaryAdapter(diaryList,MainActivity.this);
        RV_diary.setAdapter(diaryAdapter);

        CV_calendar.setOnCalendarSelectListener(this);

        navi_textsum.setTitle("已经记了"+getTextSum()+"个字");
        navi_diarycount.setTitle("已经写了"+diaryList.size()+"篇日记");

        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        int checkSdPermission = 0;
        List<String> notGrantedList = new ArrayList<>();
        for(String permission: PERMISSIONS_STORAGE) {
            checkSdPermission = checkSelfPermission(permission);
            if (checkSdPermission != PackageManager.PERMISSION_GRANTED) {
                notGrantedList.add(permission);
            }
        }
        if(notGrantedList.size() > 0){
            String[] notGrantedArray = new String[notGrantedList.size()];
            notGrantedList.toArray(notGrantedArray);
            requestPermissions(notGrantedArray, 0);
        }

    }

    private void initCalendar() {
        for(DiaryItem item:diaryList){
            map.put(getSchemeCalendar(item.getDate(),item.getMood()).toString(),getSchemeCalendar(item.getDate(),item.getMood()));
            Log.e(TAG, "initCalendar: "+getSchemeCalendar(item.getDate(),item.getMood()).toString());
        }
        CV_calendar.setSchemeDate(map);
    }


    public long getTextSum(){
        long sum = 0;
        for (DiaryItem item:diaryList){
            sum += item.getContent().length();
        }
        return sum;
    }

    private com.haibin.calendarview.Calendar getSchemeCalendar(long date, String mood){
        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        String year = new SimpleDateFormat("yyyy", Locale.CHINA).format(date);
        String month = new SimpleDateFormat("MM", Locale.CHINA).format(date);
        String day = new SimpleDateFormat("dd", Locale.CHINA).format(date);
        calendar.setYear(Integer.parseInt(year));
        calendar.setMonth(Integer.parseInt(month));
        calendar.setDay(Integer.parseInt(day));
        calendar.setScheme(mood);
        calendar.addScheme(new com.haibin.calendarview.Calendar.Scheme());
        return calendar;
    }
    private void loadDiary() {
        diaryList.clear();
        Cursor cursor = DBManager.getInstance().selectAllDiary();
        for (int i=0;i<cursor.getCount();i++){
            DiaryItem diaryItem = new DiaryItem(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),cursor.getLong(3),
                    cursor.getString(4),cursor.getString(5));
                diaryList.add(diaryItem);
                cursor.moveToNext();
        }
        cursor.close();
        if (isSameDay(Calendar.getInstance().getTimeInMillis(),diaryList.get(0).getDate())) {
            selectedItemPosition = 0;
            selectedDiaryId = diaryList.get(0).getId();
            isSelectedDateNull = false;
        }

    }

    public void removeDiaryItem(){
        diaryList.remove(selectedItemPosition);
        diaryAdapter.notifyItemRemoved(selectedItemPosition);
        diaryAdapter.notifyItemRangeChanged(selectedItemPosition,diaryAdapter.getItemCount());
        isSelectedDateNull = true;
        updateCalendar();
        navi_textsum.setTitle("已经记了"+getTextSum()+"个字");
        navi_diarycount.setTitle("已经写了"+diaryList.size()+"篇日记");
    }

    private void updateCalendar() {
        CV_calendar.clearSchemeDate();
        map.clear();
        initCalendar();
    }

    public static final String CONFIG_FIRST_START = "isFirstStart";
    private void initExample() {
        SharedPreferences mSharedPreferences = getPreferences(0);
        if (!mSharedPreferences.getBoolean(CONFIG_FIRST_START, true)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("你好。\n");
        builder.append("这是一款日记APP。\n");
        builder.append("我是这款APP的开发者，你可以看到我留下这篇日记的时间，心情、地点和天气。\n");
        builder.append("这是我的大学安卓课课设。\n");
        builder.append("你可以点击上面的黄豆进行心情设置。\n");
        builder.append("在你编辑今日日记时，如果你已经授予了定位和联网权限，会自动定位你的所在地并加载实时天气显示在上面，如果你在进行时空穿梭（编辑非今日日记），当我没说。\n");
        DiaryItem diaryItem = new DiaryItem();
        diaryItem.setMood("😊");
        diaryItem.setContent(builder.toString());
        diaryItem.setDate(1624200873678L);
        diaryItem.setLocation("江苏省·南京市\n南京邮电大学仙林校区菊苑");
        diaryItem.setWeather("阴");
        DBManager.getInstance().insert(diaryItem);
        Log.e(TAG, "insert: OK" );
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(CONFIG_FIRST_START, false);
        edit.commit();
    }

    public void initProfile() {
        String name = ProfileManager.getName(MainActivity.this);
        if(name!=null && name!=""){
            TV_profile_name.setText(name);
            TV_navi_profile_name.setText(name);
        }
    }

    public void setSelectedDiaryId(String s,int position){
        this.selectedDiaryId = s;
        this.selectedItemPosition = position;
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0://编辑日记
                Intent intent = new Intent(MainActivity.this,DiaryActivity.class);
                intent.putExtra("DiaryId",selectedDiaryId);
                intent.putExtra("SelectedPosition",selectedItemPosition);
                intent.putExtra("EditMode",true);
                startActivityForResult(intent,REQUEST_CODE_EDIT);
                break;
            case 1://删除日记
                DiaryDeleteDialog diaryDeleteDialog = new DiaryDeleteDialog(selectedDiaryId);//二次确认对话框
                diaryDeleteDialog.show(getSupportFragmentManager(),"DiaryDeleteDialog");
                break;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.LL_profile://打开抽屉菜单
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.FAB_edit://进入日历选中日记
                Intent intent= new Intent(MainActivity.this,DiaryActivity.class);
                if (!isSelectedDateNull){
                    intent.putExtra("DiaryId",selectedDiaryId);
                    intent.putExtra("SelectedPosition",selectedItemPosition);
                    intent.putExtra("EditMode",true);
                    startActivityForResult(intent,REQUEST_CODE_EDIT);
                    Log.e(TAG, "onClick: EDIT" );
                }else{
                    long date = CV_calendar.getSelectedCalendar().getTimeInMillis();
                    intent.putExtra("DiaryId","-1");
                    intent.putExtra("DiaryDate",date);
                    startActivityForResult(intent,REQUEST_CODE_ADD);
                    Log.e(TAG, "onClick: ADD");
                }
                break;
            case R.id.navi_header://修改名字、头像
                ProfileDialog profileDialog = new ProfileDialog();
                profileDialog.show(getSupportFragmentManager(),"profileDialog");
            case R.id.IV_calendar:
                if (CV_calendar.getVisibility()==View.GONE){
                    CV_calendar.setVisibility(View.VISIBLE);
                }else{
                    CV_calendar.setVisibility(View.GONE);
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD://增加日记记录
                if(resultCode == RESULT_OK){
                    DiaryItem diaryItem = (DiaryItem) data.getSerializableExtra("diaryitem");
                    int position = 0;
                    for (DiaryItem item:diaryList){//根据日期判断位置
                        position++;
                        if(diaryItem.getDate()>item.getDate()) {
                            position = diaryList.indexOf(item);
                            break;
                        }
                    }
                    diaryList.add(position,diaryItem);//插入新日记记录
                    diaryAdapter.notifyItemInserted(position);//刷新
                    diaryAdapter.notifyItemRangeChanged(position,diaryAdapter.getItemCount());
                    RV_diary.smoothScrollToPosition(position);//滚动定位至新增元素
                    updateCalendar();
                    isSelectedDateNull = false;
                    selectedItemPosition = position;
                    selectedDiaryId = diaryItem.getId();
                    navi_textsum.setTitle("已经记了"+getTextSum()+"个字");
                    navi_diarycount.setTitle("已经写了"+diaryList.size()+"篇日记");
                    Log.e(TAG, "onActivityResult: ADD"+selectedDiaryId);
                }
                break;
            case REQUEST_CODE_EDIT://更新日记记录
                if(resultCode ==RESULT_OK){
                    DiaryItem diaryItem = (DiaryItem) data.getSerializableExtra("diaryitem");
                    selectedItemPosition = data.getIntExtra("SelectedPosition",-1);
                    diaryList.set(selectedItemPosition,diaryItem);
                    diaryAdapter.notifyItemChanged(selectedItemPosition);
                    updateCalendar();
                    navi_textsum.setTitle("已经记了"+getTextSum()+"个字");
                    Log.e(TAG, "onActivityResult: "+selectedItemPosition );
                }
                break;
        }
    }

    @Override
    public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
        if(map.get(calendar.toString())!=null){
            isSelectedDateNull = false;
            int position = -1;
            for(DiaryItem item:diaryList){
                position++;
                if(isSameDay(calendar.getTimeInMillis(),item.getDate())){
                    ((LinearLayoutManager)RV_diary.getLayoutManager()).scrollToPositionWithOffset(position,0);
                    selectedItemPosition = position;
                    selectedDiaryId = item.getId();
                }
            }
        }else{isSelectedDateNull = true;}
    }
    public boolean isSameDay(long timeInMillis1,long timeInMillis2) {
        return new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(timeInMillis1).equals(new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(timeInMillis2));
    }
}