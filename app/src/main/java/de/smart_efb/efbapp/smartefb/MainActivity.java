package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    // grid view adapter
    mainMenueGridViewApdapter mainMenueGridViewApdapter;

    // title of main Menue Elements
    private String[] mainMenueElementTitle = new String [ConstansClassMain.mainMenueNumberOfElements];
    // color of active grid element
    private String[] mainMenueElementColor = new String [ConstansClassMain.mainMenueNumberOfElements];
    // color of inactive element
    private String[] mainMenueElementColorLight = new String [ConstansClassMain.mainMenueNumberOfElements];

    // background ressource of normal elements (image icon)
    private int[] mainMenueElementBackgroundRessources = new int[ConstansClassMain.mainMenueNumberOfElements];
    // background ressource of new entry elements (image icon)
    private int[] mainMenueElementBackgroundRessourcesNewEntry = new int[ConstansClassMain.mainMenueNumberOfElements];
    // background ressource of attention entry elements (image icon)
    private int[] mainMenueElementBackgroundRessourcesAttentionEntry = new int[ConstansClassMain.mainMenueNumberOfElements];
    // background ressource of elemts to show!
    private int[] mainMenueShowElementBackgroundRessources = new int[ConstansClassMain.mainMenueNumberOfElements];

    // show the menue element
    private boolean[] showMainMenueElement = new boolean[ConstansClassMain.mainMenueNumberOfElements];

    // context of main
    Context mainContext;

    // point to shared preferences
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // reference to the DB
    DBAdapter myDb;

    // activ/inactiv sub-functions
    // our arrangements sub functions activ/ inactiv
    Boolean subfunction_arrangement_comment = false;
    Boolean subfunction_arrangement_evaluation = false;
    Boolean subfunction_arrangement_sketch = false;
    Boolean subfunction_arrangement_sketchcomment = false;
    // our goals sub functions activ/ inactiv
    Boolean subfunction_goals_comment = false;
    Boolean subfunction_goals_evaluation = false;
    Boolean subfunction_goals_debetable = false;
    Boolean subfunction_goals_debetablecomment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_main);

        // register lifecycle counter for Application
        getApplication().registerActivityLifecycleCallbacks(new EfbLifecycle());

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(mainActivityBrodcastReceiver, filter);

        // init the elements arrays (title, color, colorLight, backgroundImage)
        initMainMenueElementsArrays();

        // create background ressources to show in grid
        createMainMenueElementBackgroundRessources();

        GridView gridview = (GridView) findViewById(R.id.mainMenueGridView);

        mainMenueGridViewApdapter = new mainMenueGridViewApdapter(this);

        gridview.setAdapter(mainMenueGridViewApdapter);
        gridview.setNumColumns(ConstansClassMain.numberOfGridColumns);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (showMainMenueElement[position]) {

                    Intent intent;

                    switch (position) {

                        case 0: // grid "uebergabe"
                            intent = new Intent(mainContext, ActivityConnectBook.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 1: // grid "absprachen"
                            intent = new Intent(mainContext, ActivityOurArrangement.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 2: // grid "ziele"
                            intent = new Intent(mainContext, ActivityOurGoals.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 3: // grid "nachrichten"
                            /*
                            intent = new Intent(getApplicationContext(), ActivityMeeting.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            */
                            break;
                        case 4: // grid "termine"
                            intent = new Intent(getApplicationContext(), ActivityMeeting.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 5: // grid "zeitplan"
                            intent = new Intent(getApplicationContext(), ActivityTimeTable.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 6: // grid "praevention"
                            intent = new Intent(getApplicationContext(), ActivityPrevention.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 7: // grid "faq"
                            intent = new Intent(getApplicationContext(), ActivityFaq.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 8: // grid "hilfe"
                            intent = new Intent(getApplicationContext(), ActivityEmergencyHelp.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 9:
                            // grid "einstellungen"
                            intent = new Intent(getApplicationContext(), ActivitySettingsEfb.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        default:
                            break;
                    }
                }
            }
        });


        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            // send intent to service to start the service
            Intent startServiceIntent = new Intent(getApplicationContext(), ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            getApplicationContext().startService(startServiceIntent);
        }
    }


    @Override
    public void onStart() {

        super.onStart();


        // for testing
        //prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus,0); // 0=connect to server; 1=no network available; 2=connection error; 3=connected
        //prefsEditor.commit();


        // for testing evaluation or arrangement
        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds,15); //
        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds,15); //

        prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds,15); //
        prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds,15); //

        prefsEditor.commit();



        // start exchange service with intent, when case is open!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            setAlarmForExchangeService();
        }

        // start check meeting alarm manager, when function meeting is on and case is not closed
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, false) && !prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            setAlarmForMeetingNotification();
        }

        // start check our arrangement alarm manager, when function our arrangement is on and case is not closed
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false) && !prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            setAlarmManagerForOurArrangementEvaluation();
        }

        // start check our goals alarm manager, when function our goals is on and case is not closed
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false) && !prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            setAlarmManagerForOurGoalsEvaluation();
        }

        // init array show elements
        initShowElementArray();

        // create background ressources to show in grid
        if (createMainMenueElementBackgroundRessources()) { // new things in grid?

            mainMenueGridViewApdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // de-register broadcast receiver
        this.unregisterReceiver(mainActivityBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver mainActivityBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the main view
            Boolean updateMainView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // check intent order
                // new connect book message
                String tmpExtraConnectBook = intentExtras.getString("ConnectBook","0");
                //String tmpExtraConnectBookSettings = intentExtras.getString("ConnectBookSettings","0");
                //String tmpExtraConnectBookMessageNewOrSend = intentExtras.getString("ConnectBookMessageNewOrSend","0");
                // new time table value
                String tmpExtraTimeTable = intentExtras.getString("TimeTable","0");
                //String tmpExtraTimeTableNewValue = intentExtras.getString("TimeTableNewValue","0");
                //String tmpExtraTimeTableSettings = intentExtras.getString("TimeTableSettings","0");
                // new meeting/ suggestion
                String tmpExtraMeeting = intentExtras.getString("Meeting","0");
                //String tmpExtraSuggestionNewSuggestion = intentExtras.getString("MeetingNewSuggestion","0");
                //String tmpExtraMeetingNewMeeting = intentExtras.getString("MeetingNewMeeting","0");
                //String tmpExtraSuggestionFromClientNewInvitation = intentExtras.getString("MeetingNewInvitationSuggestion","0");
                //String tmpExtraMeetingSettings = intentExtras.getString("MeetingSettings","0");
                // case is close or other menue items chnage
                String tmpExtraSettings = intentExtras.getString("Settings","0");
                //String tmpExtraSettingsOtherMenueItems = intentExtras.getString("SettingsOtherMenueItems","0");
                String tmpExtraCaseClose = intentExtras.getString("Case_close","0");

                // Settings arrangement change
                String tmpExtraOurArrangement = intentExtras.getString("OurArrangement","0");
                //String tmpExtraOurArrangementSettings = intentExtras.getString("OurArrangementSettings","0");
                // Settings goal change
                String tmpExtraOurGoal = intentExtras.getString("OurGoals","0");
                //String tmpExtraOurGoalSettings = intentExtras.getString("OurGoalsSettings","0");

                if (tmpExtraSettings != null && tmpExtraSettings.equals("1") && tmpExtraCaseClose != null && tmpExtraCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = MainActivity.this.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh view
                    updateMainView = true;

                }
                else if (tmpExtraConnectBook != null && tmpExtraConnectBook.equals("1") || tmpExtraMeeting != null && tmpExtraMeeting.equals("1") || tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") || tmpExtraOurGoal != null && tmpExtraOurGoal.equals("1") || tmpExtraSettings != null && tmpExtraSettings.equals("1") || tmpExtraTimeTable != null && tmpExtraTimeTable.equals("1")) {
                    // new update signal for connect book, meeting, our arrangement, our goal, settings, timetable -> refresh activity view
                    updateMainView = true;
                }
            }

            // update the main view
            if (updateMainView) {
                updateMainView();
            }
        }
    };


    public void updateMainView () {

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    // init the elements arrays (title, color, colorLight, backgroundImage)
    private void initMainMenueElementsArrays() {

        String[] tmpBackgroundRessources, tmpBackgroundRessourcesNewEntry, tmpBackgroundRessourcesAttentionEntry;

        // init the context
        mainContext = this;

        // get the shared preferences
        prefs = this.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // init the DB
        myDb = new DBAdapter(this);



        // for testing
        // write app version to prefs
        //prefsEditor.putInt(ConstansClassMain.namePrefsNumberAppVersion, 1);


        //prefsEditor.commit();





        // check installation status (new or update)
        newOrUpdateInstallation();

        mainMenueElementTitle = getResources().getStringArray(R.array.mainMenueElementTitle);

        mainMenueElementColor = getResources().getStringArray(R.array.mainMenueElementColor);

        mainMenueElementColorLight = getResources().getStringArray(R.array.mainMenueElementColorLight);

        tmpBackgroundRessources = getResources().getStringArray(R.array.mainMenueElementImage);
        tmpBackgroundRessourcesNewEntry = getResources().getStringArray(R.array.mainMenueElementImageNewEntry);
        tmpBackgroundRessourcesAttentionEntry = getResources().getStringArray(R.array.mainMenueElementImageAttentionEntry);

        for (int i=0; i<ConstansClassMain.mainMenueNumberOfElements; i++) {
            mainMenueElementBackgroundRessources[i] = getResources().getIdentifier(tmpBackgroundRessources[i], "drawable", "de.smart_efb.efbapp.smartefb");
            mainMenueElementBackgroundRessourcesNewEntry[i] = getResources().getIdentifier(tmpBackgroundRessourcesNewEntry[i], "drawable", "de.smart_efb.efbapp.smartefb");
            mainMenueElementBackgroundRessourcesAttentionEntry[i] = getResources().getIdentifier(tmpBackgroundRessourcesAttentionEntry[i], "drawable", "de.smart_efb.efbapp.smartefb");
        }

        // init array show elements and activ/inactiv sub-functions
        initShowElementArray();
    }


    // init array show elements
    private void initShowElementArray () {

        for (int i=0; i<ConstansClassMain.mainMenueNumberOfElements; i++) {

            String tmpMainMenueElementName = ConstansClassMain.namePrefsSubstringMainMenueElementId + i;

            showMainMenueElement[i] = false;
            if (prefs.getBoolean(tmpMainMenueElementName, false)) {
                showMainMenueElement[i] = true;
            }
        }

        // our arrangements sub functions activ/ inactiv
        subfunction_arrangement_comment = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false);
        subfunction_arrangement_evaluation = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false);
        subfunction_arrangement_sketch = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false);
        subfunction_arrangement_sketchcomment = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, false);

        // our goals sub functions activ/ inactiv
        subfunction_goals_comment = prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, false);
        subfunction_goals_evaluation = prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false);
        subfunction_goals_debetable = prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, false);
        subfunction_goals_debetablecomment = prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentDebetableGoals, false);
    }


    // creates the background ressources for the grid (like new entry or normal image)
    private boolean createMainMenueElementBackgroundRessources () {

        boolean tmpNew = false;

        for (int countElements=0; countElements < ConstansClassMain.mainMenueNumberOfElements; countElements++) {
            switch (countElements) {

                case 0: // menue item "Uebergabe"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if (myDb.getCountNewEntryConnectBookMessage() > 0) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;

                case 1: // menue item "Absprachen"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if ((subfunction_arrangement_sketchcomment && myDb.getCountAllNewEntryOurArrangementSketchComment(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0")) > 0) || ( subfunction_arrangement_comment && myDb.getCountAllNewEntryOurArrangementComment(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0")) > 0) || myDb.getCountNewEntryOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "current") > 0 || (subfunction_arrangement_sketch && myDb.getCountNewEntryOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis()), "sketch") > 0)) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;

                case 2: // menue item "Ziele"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if (myDb.getCountNewEntryOurGoals(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis())) > 0 || (subfunction_goals_comment && myDb.getCountAllNewEntryOurGoalsJointlyGoalsComment(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis())) > 0) || (subfunction_goals_debetable && myDb.getCountNewEntryOurGoals(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis())) > 0) || (subfunction_goals_debetablecomment && myDb.getCountAllNewEntryOurGoalsDebetableGoalsComment(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis())) > 0)) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;

                case 3: // menue item "Nachrichten"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 4: // menue item "Termine"
                    // delete all meeting/ suggestion mark with new but never show (time expired, etc.)
                    Long nowTime = System.currentTimeMillis();
                    myDb.deleteStatusNewEntryAllOldMeetingAndSuggestion (nowTime);
                    if (myDb.getCountNewEntryMeetingAndSuggestion("all") > 0 ) {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                    } else {
                        Cursor cSuggest = myDb.getAllRowsMeetingsAndSuggestion("suggestion_for_show_attention", nowTime);
                        Cursor cClientSuggest = myDb.getAllRowsMeetingsAndSuggestion("client_suggestion_for_show_attention", nowTime);
                        if ( (cSuggest != null && cSuggest.getCount() > 0) || (cClientSuggest != null && cClientSuggest.getCount() > 0) ) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesAttentionEntry[countElements];
                        }
                        else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                    }
                    tmpNew = true;
                    break;

                case 5: // menue item "Zeitplan"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if (prefs.getBoolean(ConstansClassTimeTable.namePrefsTimeTableNewValue, false)) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;

                case 6: // menue item "Praevention"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 7: // menue item "FAQ"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 8: // menue item "Notfallhilfe"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 9: // menue item "Einstellungen"
                    // check case close?
                    if (prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesAttentionEntry[countElements];
                    }
                    else {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    }
                    break;

                default:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
            }
        }

        return tmpNew;
    }


    // set alarm manager to start every wakeUpTimeExchangeService seconds the service to check server for new data
    public void setAlarmForExchangeService () {

        // get calendar and init
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // set calendar object with seconds
        calendar.add(Calendar.SECOND, ConstansClassMain.wakeUpTimeExchangeService);
        int tmpAlarmTime = ConstansClassMain.wakeUpTimeExchangeService * 1000; // make mills-seconds

        // make intent for alarm receiver
        Intent startIntentService = new Intent (getApplicationContext(), AlarmReceiverExchangeAndEventService.class);

        // make pending intent
        final PendingIntent pIntentService = PendingIntent.getBroadcast(this, 0, startIntentService, PendingIntent.FLAG_UPDATE_CURRENT );

        // get alarm manager service
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // set alarm manager to call exchange receiver
        try {
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpAlarmTime, pIntentService);
        }
        catch (NullPointerException e) {
            // do nothing
        }
    }


    // set alarm manager to start every wakeUpTimeMeetingNotification seconds the service to check for meetings, etc.
    public void setAlarmForMeetingNotification () {

        // get calendar and init
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // set calendar object with seconds
        calendar.add(Calendar.SECOND, ConstansClassMeeting.namePrefsMeeting_wakeUpTimeMeetingNotification);
        int tmpAlarmTime = ConstansClassMeeting.namePrefsMeeting_wakeUpTimeMeetingNotification * 1000; // make mills-seconds

        // make intent for alarm receiver
        Intent startIntentService = new Intent (getApplicationContext(), AlarmReceiverMeeting.class);

        // make pending intent
        final PendingIntent pIntentService = PendingIntent.getBroadcast(this, 0, startIntentService, PendingIntent.FLAG_UPDATE_CURRENT );

        // get alarm manager service
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // set alarm manager to call exchange receiver
        try {
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpAlarmTime, pIntentService);
        }
        catch (NullPointerException e) {
            // do nothing
        }
    }



    // set alarmmanager for our arrangement evaluation time
    void setAlarmManagerForOurArrangementEvaluation () {

        PendingIntent pendingIntentOurArrangementEvaluate;

        // get all arrangements with the same block id
        Cursor cursor = myDb.getAllRowsCurrentOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""), "equalBlockId");

        if (cursor.getCount() > 0) {

            // get reference to alarm manager
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // create intent for backcall to broadcast receiver
            Intent evaluateAlarmIntent = new Intent(this, AlarmReceiverOurArrangement.class);

            // get evaluate pause time and active time
            int evaluatePauseTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, ConstansClassOurArrangement.defaultTimeForActiveAndPauseEvaluationArrangement); // default value 43200 is 12 hours
            int evaluateActivTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, ConstansClassOurArrangement.defaultTimeForActiveAndPauseEvaluationArrangement); // default value 43200 is 12 hours

            // get start time and end time for evaluation
            Long startEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis());
            Long endEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis());

            Long tmpSystemTimeInMills = System.currentTimeMillis();
            int tmpEvalutePaAcTime = evaluateActivTime * 1000;
            String tmpIntentExtra = "evaluate";
            String tmpChangeDbEvaluationStatus = "set";
            Long tmpStartPeriod = 0L;

            // get calendar and init
            Calendar calendar = Calendar.getInstance();

            // set alarm manager when current time is between start date and end date and evaluation is enable
            if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

                calendar.setTimeInMillis(startEvaluationDate);

                do {
                    tmpStartPeriod = calendar.getTimeInMillis();
                    calendar.add(Calendar.SECOND, evaluateActivTime);
                    tmpIntentExtra = "evaluate";
                    tmpChangeDbEvaluationStatus = "set";
                    tmpEvalutePaAcTime = evaluateActivTime * 1000; // make mills-seconds
                    if (calendar.getTimeInMillis() < tmpSystemTimeInMills) {
                        tmpStartPeriod = calendar.getTimeInMillis();
                        calendar.add(Calendar.SECOND, evaluatePauseTime);
                        tmpIntentExtra = "pause";
                        tmpChangeDbEvaluationStatus = "delete";
                        tmpEvalutePaAcTime = evaluatePauseTime * 1000; // make mills-seconds
                    }
                } while (calendar.getTimeInMillis() < tmpSystemTimeInMills);

                if (tmpChangeDbEvaluationStatus.equals("delete")) {
                    // update table ourArrangement in db -> delete evaluation possible
                    myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""), "delete");
                }
                else {

                    if (cursor != null) {

                        cursor.moveToFirst();

                        do {

                            if (tmpStartPeriod > cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME))) {
                                myDb.changeStatusEvaluationPossibleOurArrangement(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)), "set");
                            } else {
                                myDb.changeStatusEvaluationPossibleOurArrangement(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)), "delete");
                            }
                        } while (cursor.moveToNext());
                    }
                }

                // put extras to intent -> "evaluate" or "delete"
                evaluateAlarmIntent.putExtra("evaluateState", tmpIntentExtra);

                // create call (pending intent) for alarm manager
                pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(this, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // set alarm
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpEvalutePaAcTime, pendingIntentOurArrangementEvaluate);
            }
            else { // delete alarm - it is out of time

                // update table ourArrangement in db -> evaluation disable
                myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""), "delete");
                // create pending intent
                pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(this, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                // delete alarm
                manager.cancel(pendingIntentOurArrangementEvaluate);
            }
        }
    }



    // set alarmmanager for our goals evaluation time
    void setAlarmManagerForOurGoalsEvaluation () {

        PendingIntent pendingIntentOurGoalsEvaluate;

        // get all jointly goals with the same block id
        Cursor cursor = myDb.getAllJointlyRowsOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""), "equalBlockId");

        if (cursor.getCount() > 0) {

            // get reference to alarm manager
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // create intent for backcall to broadcast receiver
            Intent evaluateAlarmIntent = new Intent(this, AlarmReceiverOurGoals.class);

            // get start time and end time for evaluation
            Long startEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());
            Long endEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());

            // get evaluate pause time and active time in seconds
            int evaluatePauseTime = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, ConstansClassOurGoals.defaultTimeForActiveAndPauseEvaluationJointlyGoals); // default value 43200 is 12 hours
            int evaluateActivTime = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, ConstansClassOurGoals.defaultTimeForActiveAndPauseEvaluationJointlyGoals); // default value 43200 is 12 hours

            Long tmpSystemTimeInMills = System.currentTimeMillis();
            int tmpEvalutePaAcTime = evaluateActivTime * 1000;
            String tmpIntentExtra = "evaluate";
            String tmpChangeDbEvaluationStatus = "set";
            Long tmpStartPeriod = 0L;

            // get calendar and init
            Calendar calendar = Calendar.getInstance();

            // set alarm manager when current time is between start date and end date and evaluation is enable
            if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

                calendar.setTimeInMillis(startEvaluationDate);

                do {
                    tmpStartPeriod = calendar.getTimeInMillis();
                    calendar.add(Calendar.SECOND, evaluateActivTime);
                    tmpIntentExtra = "evaluate";
                    tmpChangeDbEvaluationStatus = "set";
                    tmpEvalutePaAcTime = evaluateActivTime * 1000; // make mills-seconds
                    if (calendar.getTimeInMillis() < tmpSystemTimeInMills) {
                        tmpStartPeriod = calendar.getTimeInMillis();
                        calendar.add(Calendar.SECOND, evaluatePauseTime);
                        tmpIntentExtra = "pause";
                        tmpChangeDbEvaluationStatus = "delete";
                        tmpEvalutePaAcTime = evaluatePauseTime * 1000; // make mills-seconds
                    }
                } while (calendar.getTimeInMillis() < tmpSystemTimeInMills);

                if (tmpChangeDbEvaluationStatus.equals("delete")) {
                    // update table ourGoals in db -> delete evaluation possible
                    myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""), "delete");
                } else {

                    if (cursor != null) {

                        cursor.moveToFirst();

                        do {

                            if (tmpStartPeriod > cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME))) {
                                myDb.changeStatusEvaluationPossibleOurGoals(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID)), "set");
                            } else {
                                myDb.changeStatusEvaluationPossibleOurGoals(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID)), "delete");
                            }
                        } while (cursor.moveToNext());
                    }
                }

                // put extras to intent -> "evaluate" or "delete"
                evaluateAlarmIntent.putExtra("evaluateState", tmpIntentExtra);

                // create call (pending intent) for alarm manager
                pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(this, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // set alarm
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpEvalutePaAcTime, pendingIntentOurGoalsEvaluate);

            } else { // delete alarm - it is out of time

                // update table ourGoals in db -> evaluation disable
                myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""), "delete");
                // crealte pending intent
                pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(this, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                // delete alarm
                manager.cancel(pendingIntentOurGoalsEvaluate);
            }
        }
    }







    // inner class grid view adapter
    public class mainMenueGridViewApdapter extends BaseAdapter {

        private Context mContext;

        public mainMenueGridViewApdapter(Context c) {

            mContext = c;
        }

        @Override
        public int getCount() {

            return ConstansClassMain.mainMenueNumberOfElements;
        }

        @Override
        public Object getItem(int item) {

            return mainMenueElementBackgroundRessources[item];
        }

        @Override
        public long getItemId(int itemId) {

            return itemId;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {

            View grid;

            // init the layout color with light color
            String tmpLinearLayoutBackgroundColor = mainMenueElementColorLight[position];

            if(convertView==null){
                LayoutInflater inflater = getLayoutInflater();
                grid = inflater.inflate (R.layout.gridview_main_layout, parent, false);
            }
            else {

                grid = convertView;
            }

            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_item_image);
            LinearLayout linearLayoutView = (LinearLayout) grid.findViewById(R.id.grid_linear_layout);
            TextView txtView = (TextView) grid.findViewById(R.id.grid_item_label);

            // Element aktiv?
            if (showMainMenueElement[position]) {

                if (imageView != null) {
                    imageView.setImageResource(mainMenueShowElementBackgroundRessources[position]);
                }
                else {
                    imageView = new ImageView(mContext);
                    imageView.setId(R.id.grid_item_image);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageResource(mainMenueShowElementBackgroundRessources[position]);
                    linearLayoutView.addView(imageView,0);
                }

                // show menue name
                txtView.setText(mainMenueElementTitle[position]);
                txtView.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_white));
                tmpLinearLayoutBackgroundColor = mainMenueElementColor[position];
            }
            else { //Element is inaktiv

                // set normal background ressource (picture)
                if (imageView != null) {
                    imageView.setImageResource(mainMenueElementBackgroundRessources[position]);
                }
                else {
                    imageView = new ImageView(mContext);
                    imageView.setId(R.id.grid_item_image);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageResource(mainMenueElementBackgroundRessources[position]);
                    linearLayoutView.addView(imageView,0);
                }

                // show text "inactiv"
                txtView.setText(getResources().getString(getResources().getIdentifier("main_menue_text_inactiv", "string", getPackageName())));
                txtView.setTextColor(ContextCompat.getColor(mContext, R.color.main_menue_text_inactiv_color));
            }

            linearLayoutView.setBackgroundColor(Color.parseColor(tmpLinearLayoutBackgroundColor));

            return grid;
        }
    }


    public void newOrUpdateInstallation () {

        // check for version change
        int localAppVersionNumber = prefs.getInt(ConstansClassMain.namePrefsNumberAppVersion, 0);

        if (localAppVersionNumber < ConstansClassMain.actualAppVersionNumber ) {

            switch (localAppVersionNumber) {

                case 0: // installation of app -> first time

                    // set case close to true
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsCaseClose, false);

                    //app function switch off
                    // set function connect book off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, false); // switch off connect book

                    // set function our arrangement off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false); // turn function our arrangement off
                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false); // turn function our arrangement sketch off
                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, false); // turn function our arrangement old off

                    // set function our goals off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false); // turn function our goals off
                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, false); // turn function our goals debetable off
                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkOldGoals, false); // turn function our goals old off

                    // set function time table off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_TimeTable, false); // turn function time table off

                    // set meeting function and subfunction off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, false); // turn function meeting off
                    prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientSuggestion_OnOff, false); // turn function meeting client suggestion off
                    prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCanceleMeeting_OnOff, false); // turn function meeting client canceled meeting off
                    prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false); // turn function meeting client comment suggestion off

                    // set function message off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Message, false); // turn function message off

                    // function to switch on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Prevention, true); // turn function prevention on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Faq, true); // turn function faq on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_EmergencyHelp, true); // turn function emergency help on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Settings, true); // turn function settings on

                    // set connection parameter and status
                    prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 0); // 0=connect to server; 1=no network available; 2=connection error; 3=connected
                    prefsEditor.putInt(ConstansClassSettings.namePrefsRandomNumberForConnection, 0); // five digits for connection to server
                    prefsEditor.putString(ConstansClassSettings.namePrefsClientId, ""); // set smarthpone id to nothing


                    // set visual notification
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_ConnectBook, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangement, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangementEvaluation, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoal, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoalEvaluation, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_Message, false);
                    
                    // set acoustics notification
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_ConnectBook, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangement, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangementEvaluation, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoal, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_Message, true);

                    // write init to prefs
                    prefsEditor.commit();

                    // no break between case!
                case 1: // update one -> put new inits here

                case 2: // update two

                case 3: // update three

                case 4: // update four

                case 5: // update five and so on
            }

            // write app version to prefs
            prefsEditor.putInt(ConstansClassMain.namePrefsNumberAppVersion, ConstansClassMain.actualAppVersionNumber);
            prefsEditor.commit();
        }
    }


}