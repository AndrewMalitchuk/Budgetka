package com.example.budgetka;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.example.fragments.DashboardFragment;
import com.example.fragments.ReportFragment;
import com.example.fragments.TestFragment1;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @BindView(R.id.pager)
    ViewPager pager;

    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);//ініц.по ід

        setSupportActionBar(toolbar);//підключення тулбару

        adapter = new TabAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
                .getDisplayMetrics());//відступи між вкладками

        pager.setPageMargin(pageMargin);//установка відступів
        pager.setCurrentItem(0);//установка поточної вкладки

        setColor(ContextCompat.getColor(getBaseContext(), R.color.mainColor));//установка цвета

//        //установка події при натискані на вкладці
        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(MainActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //створення меню
        Toast.makeText(this, "onCreateOptionsMenu", Toast.LENGTH_SHORT).show();
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//      вот та дічь з іконкою
        Toast.makeText(this, "onOptionsItemSelected", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    private void setColor(int newColor) {
        //метод установки цвета
        tabs.setBackgroundColor(newColor);
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(ContextCompat.getColor(getBaseContext(), android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        getSupportActionBar().setBackgroundDrawable(ld);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        при сворачевании
        Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_SHORT).show();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        при відновленні
        Toast.makeText(this, "onRestoreInstanceState", Toast.LENGTH_SHORT).show();
        super.onRestoreInstanceState(savedInstanceState);
    }


    public class TabAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Домашня", "Звіт", "Статистика", "Что-то еще"};

        TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Toast.makeText(MainActivity.this, "getItem: " + position, Toast.LENGTH_SHORT).show();

            if(position==0){
                return DashboardFragment.newInstance();
            }else if(position==1){
                return ReportFragment.newInstance();
            }

            return TestFragment1.newInstance();

        }
    }
}
