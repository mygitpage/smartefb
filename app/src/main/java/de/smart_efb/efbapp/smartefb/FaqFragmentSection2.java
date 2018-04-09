package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 12.08.16.
 */
public class FaqFragmentSection2 extends Fragment {

    // fragment view
    View viewFragmentSection2;

    // fragment context
    Context fragmentFaqSectionTwoContext = null;

    // for prefs
    private SharedPreferences prefs;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSection2 = layoutInflater.inflate(R.layout.fragment_faq_section_2, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(faqFragmentSection2BrodcastReceiver, filter);

        return viewFragmentSection2;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentFaqSectionTwoContext = getActivity().getApplicationContext();

        // open sharedPrefs
        prefs =  fragmentFaqSectionTwoContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentFaqSectionTwoContext.MODE_PRIVATE);

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentFaqSectionTwoContext, ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            fragmentFaqSectionTwoContext.startService(startServiceIntent);
        }

        // display the view
        displayFaqSectionTwoView ("", "");
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(faqFragmentSection2BrodcastReceiver);
    }


    private BroadcastReceiver faqFragmentSection2BrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentFaqSectionTwoContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
            }
        }
    };





    void displayFaqSectionTwoView (String tmpExpandTextList, String tmpLinkTextHash) {

        String tmpText;

        TextView textViewAnswer;

        Bundle returnBundle;

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get question/ answer set 1 section 2
        tmpText = fragmentFaqSectionTwoContext.getString(R.string.faq_section2_answer1);
        textViewAnswer = (TextView) viewFragmentSection2.findViewById(R.id.faq_section2_answer1);
        returnBundle = ((ActivityFaq) getActivity()).checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            String expandTextList = ((ActivityFaq) getActivity()).getExpandTextList();
            Spanned tmpLinkText = ((ActivityFaq) getActivity()).makeLinkForMoreOrLessText(returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewAnswer.setText(tmpLinkText);
            textViewAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textViewAnswer.setText(tmpText);
        }







    }






}
