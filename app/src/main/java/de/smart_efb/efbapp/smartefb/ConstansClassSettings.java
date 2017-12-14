package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 10.02.2017.
 */
class ConstansClassSettings {

    // prefs name for connecting status
    static final String namePrefsConnectingStatus = "connectingStatus";

    // prefs name for random number for connection to server
    static final String namePrefsRandomNumberForConnection = "randomNumberForConnection";

    // prefs name for client id
    static final String namePrefsClientId = "clientId";

    // prefs name for last error messages
    static final String namePrefsLastErrorMessages = "lastError";

    // connection timeout for connection that is established in millisec
    static final int connectionEstablishedTimeOut = 15000;

    // connection timeout for connection that is read in millisec
    static final int connectionReadTimeOut = 15000;

    // URL first connect to server
    static final String urlFirstConnectToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=establish";

    // URL connection established ok
    static final String urlConnectionEstablishedToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=establishedok";

    // URL connection ask for new data
    static final String urlConnectionAskForNewDataToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=asknewdata";

    // URL connection send comment now arrangement to server
    static final String urlConnectionSendNewCommentArrangementToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=sendarrangementcomment";

    // URL connection send comment sketch arrangement to server
    static final String urlConnectionSendNewSketchCommentArrangementToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=sendsketcharrangementcomment";

    // URL connection send evaluation result arrangement to server
    static final String urlConnectionSendEvaluationResultArrangementToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=sendevaluationresultarrangement";

    // URL connection send comment jointly goals to server
    static final String urlConnectionSendNewCommentJointlyGoalsToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=sendjointlygoalscomment";

    // URL connection send evaluation result jointly goals to server
    static final String urlConnectionSendEvaluationResultJointlyGoalsToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=sendevaluationresultgoals";

    // URL connection send comment debetable goals to server
    static final String urlConnectionSendNewCommentDebetableGoalsToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=senddebetablegoalscomment";

    // URL connection send connect book message to server
    static final String urlConnectionSendConnectBookMessageToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=sendconnectbookmessage";

    // URL connection send meeting data to server
    static final String urlConnectionSendMeetingDataToServer = "https://www.smart-efb.de/index.php?com=exchange&subcom=sendmeetingdata";





}
