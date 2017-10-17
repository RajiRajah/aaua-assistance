package com.aauaforum.aauaassistance.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.aauaforum.aauaassistance.Constant;
import com.aauaforum.aauaassistance.R;
import com.aauaforum.aauaassistance.fragment.AnswerFragment;
import com.aauaforum.aauaassistance.fragment.HomeFragment;
import com.aauaforum.aauaassistance.fragment.QuestionFragment;
import com.aauaforum.aauaassistance.helper.RealmHelper;
import com.aauaforum.aauaassistance.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    @BindView(R.id.toolbar)
    Toolbar toolBar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private Realm realm;
    private RealmHelper realmHelper;

    private int[] tabIcons = {
            R.drawable.ic_chat_black_24dp,
            R.drawable.ic_live_help_black_24dp,
            R.drawable.ic_feedback_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);
        toolBar.setTitleTextColor(Color.WHITE);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


        realm = Realm.getDefaultInstance();
        realmHelper = new RealmHelper(realm);

        setupTabIcons();
        fetchUser();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor(Constant.APP_ACCENT_COLOR), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor(Constant.APP_ACCENT_COLOR),PorterDuff.Mode.SRC_IN);

                switch (tab.getPosition()){
                    case 0:
                        setTitle(getString(R.string.chatBot));
                        break;
                    case 1:
                        setTitle(getString(R.string.complains));
                        break;
                    case 2:
                        setTitle(getString(R.string.answer));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //for removing the color of first icon when switched to next tab
                tabLayout.getTabAt(0).getIcon().clearColorFilter();
                //for other tabs
                tab.getIcon().clearColorFilter();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void fetchUser() {
        if (getIntent() != null) {
            int userId = getIntent().getIntExtra(Constant.USER_ID, 0);

            User user = realmHelper.getUserById(userId);
//            String name = user.getName();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "ChatBot");
        adapter.addFragment(new QuestionFragment(), "Complains");
        adapter.addFragment(new AnswerFragment(), "Answer");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
            return null;
        }
    }
}
