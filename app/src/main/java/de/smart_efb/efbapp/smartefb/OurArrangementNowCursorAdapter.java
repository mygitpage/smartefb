package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by ich on 30.05.16.
 */
public class OurArrangementNowCursorAdapter extends CursorAdapter {

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;

    //limitation in count comments true-> yes, there is a border; no, there is no border
    Boolean commentLimitationBorder;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for arrangement (12 numbers!)
    private String[] numberCountForComments = new String [12];


    // Default constructor
    public OurArrangementNowCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);

        // init array for count comments
        numberCountForComments = context.getResources().getStringArray(R.array.ourArrangementCountComments);

        //limitation in count comments true-> yes, there is a border; no, there is no border
        commentLimitationBorder = ((ActivityOurArrangement)context).isCommentLimitationBorderSet("current");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {






        // is cursor first?
        if (cursor.isFirst() ) {
            TextView numberOfArrangement = (TextView) view.findViewById(R.id.ourArrangementIntroText);
            String txtArrangementNumber = context.getResources().getString(R.string.ourArrangementIntroText) + " " + EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy");
            numberOfArrangement.setText(txtArrangementNumber);
        }

        // is cursor last?
        if (cursor.isLast() ) { // listview for last element -> set gap to bottom of display
            TextView tmpGapToBottom = (TextView) view.findViewById(R.id.borderToBottomOfDisplayWhenNeeded);
            tmpGapToBottom.setVisibility(View.VISIBLE);
        }












    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        // link to show comments
        Spanned showCommentsLinkTmp = null;
        // link to comment an arrangement
        Spanned showCommentArrangementLinkTmp = null;
        // link to evaluate arrangement
        Spanned showEvaluateCommentLinkTmp = null;

        // text for new comment entry
        String tmpTextNewEntryComment = "";


        if (cursor.isFirst() ) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_now_first, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_now, parent, false);
        }




        // put arrangement number
        TextView numberOfArrangement = (TextView) inflatedView.findViewById(R.id.listArrangementNumberText);
        String txtArrangementNumber = context.getResources().getString(R.string.showArrangementIntroText)+ " " + Integer.toString(cursor.getPosition()+1);
        numberOfArrangement.setText(txtArrangementNumber);

        // put author name
        TextView tmpTextViewAuthorNameText = (TextView) inflatedView.findViewById(R.id.listTextAuthorName);
        String tmpTextAuthorNameText = String.format(context.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

        // check if arrangement entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfArrangement = (TextView) inflatedView.findViewById(R.id.listArrangementNewArrangementText);
            String txtnewEntryOfArrangement = context.getResources().getString(R.string.newEntryText);
            newEntryOfArrangement.setText(txtnewEntryOfArrangement);
            myDb.deleteStatusNewEntryOurArrangement(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // put arrangement text
        TextView textViewArrangement = (TextView) inflatedView.findViewById(R.id.listTextArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);

        // generate link for evaluate an arrangement
        TextView linkEvaluateAnArrangement = (TextView) inflatedView.findViewById(R.id.linkToEvaluateAnArrangement);
        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) {

            // make link to evaluate arrangement, when evaluation is possible for this arrangement
            if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE)) == 1) {
                Uri.Builder evaluateLinkBuilder = new Uri.Builder();
                evaluateLinkBuilder.scheme("smart.efb.deeplink")
                        .authority("linkin")
                        .path("ourarrangement")
                        .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID))))
                        .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition() + 1))
                        .appendQueryParameter("com", "evaluate_an_arrangement");

                showEvaluateCommentLinkTmp = Html.fromHtml("<a href=\"" + evaluateLinkBuilder.build().toString() + "\">" + context.getResources().getString(context.getResources().getIdentifier("ourArrangementEvaluateString", "string", context.getPackageName())) + "</a>");
            } else { // link is not possible, so do it with text

                showEvaluateCommentLinkTmp = Html.fromHtml("(" + context.getResources().getString(context.getResources().getIdentifier("ourArrangementEvaluateString", "string", context.getPackageName())) + ")");
            }

            linkEvaluateAnArrangement.setText(showEvaluateCommentLinkTmp);
            linkEvaluateAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());

            /*
            // calculate run time for timer in MILLISECONDS!!!
            Long nowTime = System.currentTimeMillis();
            Long writeTimeComment = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME));
            Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) * 60000; // make milliseconds from miutes
            Long runTimeForTimer = delayTime - (nowTime - writeTimeComment);
            // start the timer with the calculated milliseconds
            if (runTimeForTimer > 0) {
                new CountDownTimer(runTimeForTimer, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // gernate count down timer
                        String FORMAT = "%02d:%02d:%02d";
                        String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendDelayInfo);
                        String tmpTime = String.format(FORMAT,
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        // put count down to string
                        String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                        // and show
                        tmpTextViewSendInfoLastActualComment.setText(tmpCountdownTimerString);
                    }

                    public void onFinish() {
                        // count down is over -> show
                        String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                    }
                }.start();

            }
            else {
                // no count down anymore -> show send successfull
                String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
            }
             */





        }
        else { // evaluation not possible/ deactivated
            linkEvaluateAnArrangement.setVisibility(View.GONE);
        }

        // Show link for comment an arrangement and to show all comments for an arrangement
        TextView linkCommentAnArrangement = (TextView) inflatedView.findViewById(R.id.linkCommentAnArrangement);
        TextView linkShowCommentOfArrangement = (TextView) inflatedView.findViewById(R.id.linkToShowCommentsOfArrangements);

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false)) {

            // get from DB  all comments for choosen arrangement (getCount)
            Cursor cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
            // generate the number of comments to show
            String tmpCountComments;
            int tmpIntCountComments = cursorArrangementAllComments.getCount();
            if (cursorArrangementAllComments.getCount() > 10) {
                tmpCountComments = numberCountForComments[11];

            }
            else {
                tmpCountComments = numberCountForComments[cursorArrangementAllComments.getCount()];
            }

            // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
            if (cursorArrangementAllComments.getCount() > 0) {
                cursorArrangementAllComments.moveToFirst();
                if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
                    tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(context, R.color.text_accent_color) + "'>"+ context.getResources().getString(R.string.newEntryText) + "</font>";
                }
            }

            // make link to comment arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "comment_an_arrangement");

            // make link to show comment for arrangement
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "show_comment_for_arrangement");;


            if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) || !commentLimitationBorder) {
                showCommentArrangementLinkTmp = Html.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+"</a>");
            }
            else {
                showCommentArrangementLinkTmp = Html.fromHtml(" ("+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+")");
            }
            linkCommentAnArrangement.setText(showCommentArrangementLinkTmp);
            linkCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());


            if (tmpIntCountComments == 0) {
                showCommentsLinkTmp = Html.fromHtml(tmpCountComments);
            }
            else {
                showCommentsLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountComments + "</a> " + tmpTextNewEntryComment);
            }
            linkShowCommentOfArrangement.setText(showCommentsLinkTmp);
            linkShowCommentOfArrangement.setMovementMethod(LinkMovementMethod.getInstance());

        }
        else { // comment and show comment are deactivated -> hide them

            linkCommentAnArrangement.setVisibility(View.GONE);
            linkShowCommentOfArrangement.setVisibility(View.GONE);


        }




        /*

        // show generate links for evaluate or/and comment
        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false) || prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false) ) {

            // create the comment link
            TextView linkCommentAnArrangement = (TextView) inflatedView.findViewById(R.id.linkCommentAnArrangement);

            if (showEvaluateCommentLinkTmp != null && showCommentsLinkTmp != null && showCommentArrangementLinkTmp != null) {
                linkCommentAnArrangement.setText(TextUtils.concat(showEvaluateCommentLinkTmp, showCommentsLinkTmp, showCommentArrangementLinkTmp));
            }
            else if (showEvaluateCommentLinkTmp == null && showCommentsLinkTmp != null && showCommentArrangementLinkTmp != null) {
                linkCommentAnArrangement.setText(TextUtils.concat(showCommentsLinkTmp, showCommentArrangementLinkTmp));
            }
            else {
                linkCommentAnArrangement.setText(showEvaluateCommentLinkTmp);
            }

            linkCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());

        }
        */


        return inflatedView;

    }


    // Turn off view recycling in listview, because there are different views (first, normal)
    // getViewTypeCount(), getItemViewType
    @Override
    public int getViewTypeCount () {

        return getCount();
    }

    @Override
    public int getItemViewType (int position) {

        return position;
    }

}
