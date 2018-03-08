package com.devdelhi.pointgram.pointgram.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.devdelhi.pointgram.pointgram.Fragments.Fragment_Alarms;
import com.devdelhi.pointgram.pointgram.Fragments.Fragment_Chats;
import com.devdelhi.pointgram.pointgram.Fragments.Fragment_MyActivity;
import com.devdelhi.pointgram.pointgram.Fragments.Fragment_Requests;

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
                Fragment_Alarms alarmFragments = new Fragment_Alarms();
                return alarmFragments;

            case 1:
                Fragment_Chats chatFragment  = new Fragment_Chats();
                return chatFragment;

            case 2:
                Fragment_Requests requestsFragment = new Fragment_Requests();
                return requestsFragment;

            case 3:
                Fragment_MyActivity myActivity = new Fragment_MyActivity();
                return myActivity;
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
                return "Alarms";
            case 1:
                return "Friends";
            case 2:
                return "Requests";
            case 3:
                return "Activity";
            default:
                return null;
        }
    }
}
