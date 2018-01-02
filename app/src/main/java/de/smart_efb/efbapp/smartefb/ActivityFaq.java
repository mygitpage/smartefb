package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 12.08.16.
 */
public class ActivityFaq extends AppCompatActivity {

    Toolbar toolbarFaq;
    ActionBar actionBar;

    ViewPager viewPagerFaq;
    TabLayout tabLayoutFaq;

    // reference to dialog settings
    AlertDialog alertDialogFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_faq);

        // init activity faq
        initFaq();

        // set up viewpager
        viewPagerFaq = (ViewPager) findViewById(R.id.viewPagerFaq);
        FaqViewPagerAdapter faqViewPagerAdapter = new FaqViewPagerAdapter(getSupportFragmentManager(), this);
        viewPagerFaq.setAdapter(faqViewPagerAdapter);

        tabLayoutFaq = (TabLayout) findViewById(R.id.tabLayoutFaq);
        tabLayoutFaq.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayoutFaq.setupWithViewPager(viewPagerFaq);

        tabLayoutFaq.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tmpSubtitleText = "";

                // Change the subtitle of the activity
                switch (tab.getPosition()) {
                    case 0: // title for tab zero
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionOne", "string", getPackageName()));
                        break;
                    case 1: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionTwo", "string", getPackageName()));
                        break;
                    case 2: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionThree", "string", getPackageName()));
                        break;
                    case 3: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionFour", "string", getPackageName()));
                        break;
                    case 4: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionFive", "string", getPackageName()));
                        break;
                    default:
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionOne", "string", getPackageName()));
                        break;
                }

                // set correct subtitle in toolbar
                toolbarFaq.setSubtitle(tmpSubtitleText);

                // set correct tab over viewpager
                viewPagerFaq.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // check for intent on start time
        // Extras from intent that holds data
        Bundle intentExtras = null;
        // intent
        Intent intent = getIntent();

        if (intent != null) { // intent set?
            // get the link data from the extra
            intentExtras = intent.getExtras();
            if (intentExtras != null && intentExtras.getString("com") != null) { // extra data set?
                // get command and execute it
                executeIntentCommand(intentExtras.getString("com"));
            }
        }
    }


    // init the activity Faq
    private void initFaq() {

        // init the toolbar
        toolbarFaq = (Toolbar) findViewById(R.id.toolbarFaq);
        setSupportActionBar(toolbarFaq);
        toolbarFaq.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set correct subtitle for first call
        String tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionOne", "string", getPackageName()));
        toolbarFaq.setSubtitle(tmpSubtitleText);

        // create help dialog
        createHelpDialog();
    }


    // Look for new intents (with data from putExtra)
    @Override
    protected void onNewIntent(Intent intent) {

        // Extras from intent that holds data
        Bundle intentExtras = null;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        intentExtras = intent.getExtras();

        if (intentExtras != null) {

            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"));
        }
    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command) {

        if (command.equals("show_section1")) { // Show fragment for overview

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(0);
            tab.select();
        }
        else if (command.equals("show_section2")) { // Show fragment for Fragen zur App

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(1);
            tab.select();
        }
        else if (command.equals("show_section3")) { // Show fragment for Erziehungsberatung

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(2);
            tab.select();
        }
        else if (command.equals("show_section4")) { // Show fragment for Beratungsstellen

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(3);
            tab.select();
        }
        else if (command.equals("show_section5")) { // Show fragment for Erziehungsfragen

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(4);
            tab.select();
        }
        else { // default is overview

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(0);
            tab.select();
        }
    }


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonSettings = (Button) findViewById(R.id.helpFaq);

        // add button listener to question mark in activity settings efb (toolbar)
        tmpHelpButtonSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFaq.this);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityFaq.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_faq, null);

                // get string ressources
                String tmpTextCloseDialog = ActivityFaq.this.getResources().getString(R.string.textDialogSettingsEfbCloseDialog);
                String tmpTextTitleDialog = ActivityFaq.this.getResources().getString(R.string.textDialogSettingsEfbTitleDialog);

                // build the dialog
                builder.setView(dialogSettings)

                        // Add close button
                        .setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogFaq.cancel();
                            }
                        })

                        // add title
                        .setTitle(tmpTextTitleDialog);

                // and create
                alertDialogFaq = builder.create();

                // and show the dialog
                builder.show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}