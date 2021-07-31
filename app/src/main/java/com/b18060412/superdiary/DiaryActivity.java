package com.b18060412.superdiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.b18060412.superdiary.db.DBManager;
import com.b18060412.superdiary.db.DiaryItem;
import com.b18060412.superdiary.dialog.DiarySaveDialog;
import com.b18060412.superdiary.fragment.DiaryEditorFragment;
import com.b18060412.superdiary.fragment.DiaryViewerFragment;

import java.util.Calendar;

import info.hoang8f.android.segmented.SegmentedGroup;

public class DiaryActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "DiaryActivity";//  日记浏览与编辑

    private DiaryItem diaryItem; //主数据体
    private String diaryId = new String(); //日记记录ID
    private Boolean isAdd = false;


    private ViewPager VP_diary_content;
    private SegmentedGroup SG_diary_topbar;
    private RadioButton BT_diary_viewer, BT_diary_editor;//单选框
    private TextView TV_diary_day,TV_diary_date,TV_diary_weather,TV_diary_mood,TV_diary_location;


    private ScreenSlidePagerAdapter slidePagerAdapter;

    public DiaryItem getDiaryItem() {
        return diaryItem;
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        VP_diary_content = findViewById(R.id.VP_diary_content);
        SG_diary_topbar = findViewById(R.id.SG_diary_topbar);
        SG_diary_topbar.setOnCheckedChangeListener(this);
        BT_diary_editor = findViewById(R.id.BT_diary_editor);
        BT_diary_viewer = findViewById(R.id.BT_diary_viewer);
        TV_diary_date = findViewById(R.id.TV_diary_date);
        TV_diary_day = findViewById(R.id.TV_diary_day);
        TV_diary_mood = findViewById(R.id.TV_diary_mood);
        registerForContextMenu(TV_diary_mood);
        TV_diary_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });
        TV_diary_location = findViewById(R.id.TV_diary_location);
        TV_diary_weather = findViewById(R.id.TV_diary_weather);
        diaryId = getIntent().getStringExtra("DiaryId");
        if (!diaryId.equals("-1")) {
            diaryItem = DBManager.getInstance().getById(diaryId);
            if (getIntent().getBooleanExtra("EditMode",false)){
                editMode();
            }else{
                initViewPager();
                BT_diary_viewer.setChecked(true);
            }
        }else{
            long date = getIntent().getLongExtra("DiaryDate",-1);
            if (date == 0){date = Calendar.getInstance().getTimeInMillis();}
            diaryItem = new DiaryItem(date);
            isAdd = true;
            editMode();
            if (Math.abs((diaryItem.getDate() - Calendar.getInstance().getTimeInMillis()) / (1000 * 3600 * 24)) < 1){
                getLocation();
            }
        }
        TV_diary_mood.setText(diaryItem.getMood());
        TV_diary_date.setText(diaryItem.getDateInfo());
        TV_diary_day.setText(diaryItem.getDay());
        TV_diary_location.setText(diaryItem.getLocation());
        TV_diary_weather.setText(diaryItem.getWeather());
    }
    public void editMode(){
        initViewPager();
        BT_diary_editor.setChecked(true);
        VP_diary_content.setCurrentItem(1);
    }



    private void initViewPager() {
        VP_diary_content.setOffscreenPageLimit(1);
        slidePagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        VP_diary_content.setAdapter(slidePagerAdapter);
        VP_diary_content.addOnPageChangeListener(onPageChangeListener);
    }

    private void showPopupMenu(){
        PopupMenu popupMenu = new PopupMenu(this,TV_diary_mood);
        popupMenu.inflate(R.menu.mood_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    default:
                        TV_diary_mood.setText(menuItem.getTitle());
                        diaryItem.setMood((String) menuItem.getTitle());
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                default:
                    BT_diary_viewer.setChecked(true);
                    DiaryViewerFragment fragmentViewer = (DiaryViewerFragment) slidePagerAdapter.getItem(position);
                    Fragment fragmentEditor = slidePagerAdapter.getItem(1);
                    fragmentViewer.setContent(fragmentEditor);
                    hintKeyBoard();
                    break;
                case 1:
                    BT_diary_editor.setChecked(true);
                    break;
            }
        }
    };

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.BT_diary_viewer:
                VP_diary_content.setCurrentItem(0);
                break;
            case R.id.BT_diary_editor:
                VP_diary_content.setCurrentItem(1);
                break;
        }
    }
    public void hintKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private DiaryViewerFragment diaryViewerFragment = new DiaryViewerFragment();
    private DiaryEditorFragment diaryEditorFragment = new DiaryEditorFragment();

    public void getLocation(){
        AMapLocationClient locationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        AMapLocationListener mapLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                Toast.makeText(DiaryActivity.this, "定位中", Toast.LENGTH_SHORT).show();
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        String province = aMapLocation.getProvince();//省信息
                        String city = aMapLocation.getCity();//城市信息
                        getWeather(aMapLocation.getAdCode());
                        String poi = aMapLocation.getPoiName();//获取当前定位点的POI信息
                        TV_diary_location.setText(province+"·"+city+"\n"+poi);
                        locationClient.stopLocation();
                        locationClient.onDestroy();
                        }
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("地图错误","定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                                + aMapLocation.getErrorInfo());
                        Toast.makeText(DiaryActivity.this, "\"定位失败, 错误码:\" + aMapLocation.getErrorCode() + \", 错误信息:\"\n" +
                            "                                + aMapLocation.getErrorInfo()", Toast.LENGTH_SHORT).show();
                    }
                }
        };
        locationClient.setLocationListener(mapLocationListener);
        locationClientOption.setOnceLocation(true);
        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();

    }

    public void getWeather(String city){
        WeatherSearch weatherSearch = new WeatherSearch(this);
        weatherSearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
                if (i == 1000) {
                    if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                        LocalWeatherLive weatherlive = localWeatherLiveResult.getLiveResult();
                        TV_diary_weather.setText(weatherlive.getWeather());
                    }
                }
            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

            }
        });
        WeatherSearchQuery query;
        query = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        weatherSearch.setQuery(query);
        weatherSearch.searchWeatherAsyn();
    }


    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                default:
                    fragment = diaryViewerFragment;
                    break;
                case 1:
                    fragment = diaryEditorFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        updateDiaryContent();
        if (keyCode==KeyEvent.KEYCODE_BACK && !diaryItem.equals(DBManager.getInstance().getById(diaryId))||isAdd){
            Log.e(TAG, "onKeyDown: "+isAdd );
            DiarySaveDialog diarySaveDialog = new DiarySaveDialog();
            diarySaveDialog.show(getSupportFragmentManager(),"DiarySaveDialog");
        }else{ finish();}
        return false;
    }

    public void updateDiaryContent(){//更新日记内容
        diaryItem.setContent(this.diaryEditorFragment.getContent());
        diaryItem.setMood(TV_diary_mood.getText().toString());
        diaryItem.setWeather(TV_diary_weather.getText().toString());
        diaryItem.setLocation(TV_diary_location.getText().toString());
    }
}