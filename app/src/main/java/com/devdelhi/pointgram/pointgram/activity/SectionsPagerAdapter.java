package com.devdelhi.pointgram.pointgram.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.devdelhi.pointgram.pointgram.fragments.Fragment_Alarms;
import com.devdelhi.pointgram.pointgram.fragments.Fragment_Chats;
import com.devdelhi.pointgram.pointgram.fragments.Fragment_MyActivity;
import com.devdelhi.pointgram.pointgram.fragments.Fragment_Requests;

/**
 * Created by deejay on 18/2/18.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter{
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment_Chats chatFragment  = new Fragment_Chats();
                return chatFragment;

            case 1:
                Fragment_Alarms alarmFragments = new Fragment_Alarms();
                return alarmFragments;

            case 2:
                Fragment_MyActivity myActivity = new Fragment_MyActivity();
                return myActivity;


            case 3:
                Fragment_Requests requestsFragment = new Fragment_Requests();
                return requestsFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle (int position) {
        switch (position) {
            case 0:
                return "Friends";
            case 1:
                return "Alarms";
            case 2:
                return "Activity";
            case 3:
                return "Requests";
            default:
                return null;
        }
    }
}
