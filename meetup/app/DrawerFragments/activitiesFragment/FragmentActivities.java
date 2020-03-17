package com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rubenmimoun.meetup.app.R;

public class FragmentActivities extends Fragment {

    public FragmentActivities(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v  =  inflater.inflate(R.layout.fragment_activities,container,false);


        System.out.println("ON FRAGMENT ACTIVITY");

        TabLayout tabLayout = v.findViewById(R.id.tab_layout) ;
        ViewPager viewPager =v.findViewById(R.id.viewpager_actitivy);
        TabLayoutAdapter layoutAdapter = new TabLayoutAdapter(getChildFragmentManager());

        viewPager.setAdapter(layoutAdapter);

         tabLayout.setupWithViewPager(viewPager);


        return  v ;
    }
    class TabLayoutAdapter extends FragmentStatePagerAdapter {

        private String [] pagesName = {"Create","Join"};

        public TabLayoutAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return pagesName[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position){
                default:
                case 0 :
                    return new FragmentCreateActivity();
                case 1 :

                    return  new FragmentActiviesToJoin();
            }


        }

        @Override
        public int getCount() {
            return pagesName.length;
        }


    }

}
