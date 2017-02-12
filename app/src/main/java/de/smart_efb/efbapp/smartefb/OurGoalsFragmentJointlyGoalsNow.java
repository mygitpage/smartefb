package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ich on 16.10.2016.
 */
public class OurGoalsFragmentJointlyGoalsNow extends Fragment {


    // fragment view
    View viewFragmentJointlyGoalsNow;

    // fragment context
    Context fragmentJointlyGoalsNowContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of jointly goals -> the other are old (look at tab old)
    long currentDateOfJointlyGoals;

    // reference cursorAdapter for the listview
    OurGoalsJointlyGoalsNowCursorAdapter dataAdapterListViewOurGoals = null;

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentJointlyGoalsNow = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_now, null);

        // register broadcast receiver and intent filter for action GOALS_EVALUATE_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("GOALS_EVALUATE_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentJointlyGoalsNowBrodcastReceiver, filter);

        return viewFragmentJointlyGoalsNow;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentJointlyGoalsNowContext = getActivity().getApplicationContext();

        // init the fragment jointly goals now
        initFragmentJointlyGoalsNow();

        // show actual jointly goals set
        displayActualJointlyGoalsSet();

    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        //de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentJointlyGoalsNowBrodcastReceiver);

    }


    // Broadcast receiver for action GOALS_EVALUATE_STATUS_UPDATE -> comes from alarmmanager ourGoals
    private BroadcastReceiver ourGoalsFragmentJointlyGoalsNowBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // notify listView that data has changed (evaluate-link is activ or passiv)
            if (dataAdapterListViewOurGoals != null) {
                // get new data from db
                Cursor cursor = myDb.getAllJointlyRowsOurGoals(currentDateOfJointlyGoals, "equal");

                // and notify listView
                dataAdapterListViewOurGoals.changeCursor(cursor);
                dataAdapterListViewOurGoals.notifyDataSetChanged();

            }

        }
    };


    // inits the fragment for use
    private void initFragmentJointlyGoalsNow() {

        // init the DB
        myDb = new DBAdapter(fragmentJointlyGoalsNowContext);

        // init the prefs
        prefs = fragmentJointlyGoalsNowContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentJointlyGoalsNowContext.MODE_PRIVATE);

        //get current date of jointly goals
        currentDateOfJointlyGoals = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());

        // ask methode isCommentLimitationBorderSet() in ActivityOurGoals to limitation in comments? true-> yes, linitation; false-> no
        commentLimitationBorder = ((ActivityOurGoals) getActivity()).isCommentLimitationBorderSet("current");

    }


    // show listView with current goals or info: mothing there
    public void displayActualJointlyGoalsSet () {

        Cursor cursor = myDb.getAllJointlyRowsOurGoals(currentDateOfJointlyGoals, "equal");

        // find the listview
        ListView listView = (ListView) viewFragmentJointlyGoalsNow.findViewById(R.id.listOurGoalsJointlyGoalsNow);

        if (cursor.getCount() > 0 && listView != null) {

            // set listView visible and textView hide
            setVisibilityListViewJointlyGoalsNow("show");
            setVisibilityTextViewTextNotAvailable("hide");

            // Set correct subtitle in Activity -> "Gemeinsame Ziele vom ..."
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleJointlyGoalsNow", "string", fragmentJointlyGoalsNowContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy");
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyNow");

            // new dataadapter
            dataAdapterListViewOurGoals = new OurGoalsJointlyGoalsNowCursorAdapter(
                    getActivity(),
                    cursor,
                    0);

            // Assign adapter to ListView
            listView.setAdapter(dataAdapterListViewOurGoals);

        }
        else {

            // set listView hide and textView visible
            setVisibilityListViewJointlyGoalsNow("hide");
            setVisibilityTextViewTextNotAvailable("show");

            // Set correct subtitle in Activity -> "Keine gemeinsamen Ziele vorhanden"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleNothingThere", "string", fragmentJointlyGoalsNowContext.getPackageName()));
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyNow");

        }

    }


    // set visibility of listViewOurGoals
    private void setVisibilityListViewJointlyGoalsNow (String visibility) {

        ListView tmplistView = (ListView) viewFragmentJointlyGoalsNow.findViewById(R.id.listOurGoalsJointlyGoalsNow);

        if (tmplistView != null) {

            switch (visibility) {

                case "show":
                    tmplistView.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmplistView.setVisibility(View.GONE);
                    break;

            }
        }

    }


    // set visibility of textView "nothing there"
    private void setVisibilityTextViewTextNotAvailable (String visibility) {

        TextView tmpNotAvailable = (TextView) viewFragmentJointlyGoalsNow.findViewById(R.id.textViewJointlyGoalsNowNothingThere);

        if (tmpNotAvailable != null) {

            switch (visibility) {

                case "show":
                    tmpNotAvailable.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmpNotAvailable.setVisibility(View.GONE);
                    break;

            }

        }

    }


    // geter for border for comments
    public boolean isCommentLimitationBorderSet () {

        // true-> comments are limited; false-> no limit
        return  commentLimitationBorder;

    }



}
