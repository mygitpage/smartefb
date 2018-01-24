package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 06.02.2017.
 */
 class ConstansClassMeeting {

   // Number of different subtitles
   static final int numberOfDifferentSubtitle = 8;

   // show max numbers of suggestions
   static final int maxNumbersOfSuggestion = 6;

   // show time for canceled meetings [in milliseconds]
   static final Long showDifferentTimeForCanceledMeeting = 1209600000L; // (two weeks)

   // meeting client suggestion on off to prefs
   static final String namePrefsMeeting_ClientSuggestion_OnOff ="meetingClientSuggestionOnOff";

   // meeting client cancele meeting on off to prefs
   static final String namePrefsMeeting_ClientCanceleMeeting_OnOff ="meetingClientCanceleMeetingOnOff";

   // meeting client comment suggestion on off to prefs
   static final String namePrefsMeeting_ClientCommentSuggestion_OnOff ="meetingClientCommentSuggestionOnOff";

   // meeting client wake up time for meetings, etc.
   static final int namePrefsMeeting_wakeUpTimeMeetingNotification = 60; // in seconds

   // meeting number of letters for canceled meeting reason
   static final int namePrefsMaxLettersCanceledMeetingReason = 600;

   // suggestion comment max letters
   static final int namePrefsSuggestionCommentMaxLetters = 200;

   // suggestion comment max letters
   static final int namePrefsSuggestionFromClientMaxLetters = 600;

}
