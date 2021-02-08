package com.genedev.retinalclassifierfull.AssistantHelpers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.Activities.Home;
import com.genedev.retinalclassifierfull.R;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.google.gson.Gson;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.android.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class Assistant extends AppCompatActivity implements AIListener {

    private ChatView mChatView;

    private Assistant context = this;

    public static final String TAG = "AI";

    private Gson gson = GsonFactory.getGson();

    private TextView resultTextView;
    private EditText contextEditText;
    private EditText queryEditText;
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    private AIService aiService;

    private Animation fadeIn,fadeOut;
    private AnimationSet animation;

    private Keyboard keyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        final AIConfiguration config = new AIConfiguration("23610cbd97154b36a582311558f3641b",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(config);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator()); //add this
        fadeIn.setDuration(500);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateDecelerateInterpolator()); //add this
        fadeOut.setDuration(300);
        //User id
        int myId = 0;
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        //User name
        String myName = "You";

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_assistant_profile_medium);
        String yourName = "Assistant";

        final User me = new User(myId, myName, myIcon);
        final User you = new User(yourId, yourName, yourIcon);

        mChatView = findViewById(R.id.chat_view);
        //Set UI parameters if you need
        mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.right_bubble_bg_color));
        mChatView.setLeftBubbleColor(ContextCompat.getColor(this, R.color.navBarBackground));
        mChatView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mChatView.setMessageFontSize(24);
        mChatView.setSendIcon(R.drawable.ic_check_circle_green_a400_24dp);
        mChatView.setSendButtonColor(R.color.colorPrimary);
        mChatView.setDateSeparatorFontSize(0);
        mChatView.setTimeLabelFontSize(0);
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(R.color.navBarBackground);
        mChatView.setDateSeparatorFontSize(0);
        mChatView.setInputTextHint("Ask something.");
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);
        mChatView.setEnableSwipeRefresh(true);
        mChatView.setSoundEffectsEnabled(true);

        final Message receivedMessage = new Message.Builder()
                .setUser(you)
                .setRight(false)
                .setText("Hi, How can I help?")
                .hideIcon(false)
                .setUsernameVisibility(false)
                .build();
        mChatView.receive(receivedMessage);
        //Click Send Button

        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new message

                sendRequest();
                Message message = new Message.Builder()
                        .setUser(me)
                        .setUsernameVisibility(false)
                        .setRight(true)
                        .setText(mChatView.getInputText())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                mChatView.send(message);
                //Reset edit text
                mChatView.setInputText("");
                if (message.getText().toString().matches("Open Google Maps")){
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=Hospitals with Panretinal Laser Surgery");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                }
            }
        });

        final Button what_can_you_do = findViewById(R.id.what_can_you_do);
        what_can_you_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                Message message = new Message.Builder()
                        .setUser(me)
                        .setUsernameVisibility(false)
                        .setRight(true)
                        .setText(what_can_you_do.getText().toString())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                //mChatView.send(message);
                //Reset edit text
                mChatView.setInputText(what_can_you_do.getText().toString());
            }
        });

        final Button solution = findViewById(R.id.solutions);
        solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                Message message = new Message.Builder()
                        .setUser(me)
                        .setUsernameVisibility(false)
                        .setRight(true)
                        .setText(solution.getText().toString())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                //mChatView.send(message);
                //Reset edit text
                mChatView.setInputText(solution.getText().toString());
            }
        });

        final Button near = findViewById(R.id.nearest_opthal);
        near.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                Message message = new Message.Builder()
                        .setUser(me)
                        .setUsernameVisibility(false)
                        .setRight(true)
                        .setText(near.getText().toString())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                //mChatView.send(message);
                //Reset edit text
                mChatView.setInputText(near.getText().toString());

            }
        });
        final Button about_dr = findViewById(R.id.about_dr);
        about_dr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                Message message = new Message.Builder()
                        .setUser(me)
                        .setUsernameVisibility(false)
                        .setRight(true)
                        .setText(about_dr.getText().toString())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                //mChatView.send(message);
                //Reset edit text
                mChatView.setInputText(about_dr.getText().toString());
            }
        });
        final Button help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                Message message = new Message.Builder()
                        .setUser(me)
                        .setUsernameVisibility(false)
                        .setRight(true)
                        .setText(help.getText().toString())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                //mChatView.send(message);
                //Reset edit text
                mChatView.setInputText(help.getText().toString());
            }
        });
    }

    private void sendRequest() {
        final String queryString = String.valueOf(mChatView.getInputText());
        @SuppressLint("StaticFieldLeak")
        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {
            private AIError aiError;
            @Override
            protected AIResponse doInBackground(final String... params) {

                final AIRequest request = new AIRequest();

                String query = params[0];

                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);
                try {
                    return aiDataService.request(request);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }
            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };
        task.execute(queryString);
    }

    @Override
    public void onResult(final AIResponse response) {
        Result result = response.getResult();

        final String speech = result.getFulfillment().getSpeech();
        // Show results in TextView.
        //results.setText(speech);
        Button suggestion1 = findViewById(R.id.what_can_you_do);
        Button suggestion2 = findViewById(R.id.solutions);
        Button suggestion3 = findViewById(R.id.nearest_opthal);
        Button suggestion4 = findViewById(R.id.about_dr);
        Button suggestion5 = findViewById(R.id.help);

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_assistant_profile_medium);
        String yourName = "Assistant";

        int myId = 0;
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String myName = "You";

        final User me = new User(myId, myName, myIcon);

        final User you = new User(yourId, yourName, yourIcon);
        //Receive message
        final Message receivedMessage = new Message.Builder()
                .setUser(you)
                .setRight(false)
                .setText(speech)
                .hideIcon(false)
                .setUsernameVisibility(false)
                .build();
        mChatView.receive(receivedMessage);

        TextView no_parameters = findViewById(R.id.no_parameters);

        String entries_1 = result.getStringParameter("s_1");
        String entries_2 = result.getStringParameter("s_2");
        String entries_3 = result.getStringParameter("s_3");
        String entries_4 = result.getStringParameter("s_4");
        String entries_5 = result.getStringParameter("s_5");

        suggestion1.setText(entries_1);
        suggestion1.startAnimation(fadeIn);

        suggestion2.setText(entries_2);
        suggestion2.startAnimation(fadeIn);

        suggestion3.setText(entries_3);
        suggestion3.startAnimation(fadeIn);

        suggestion4.setText(entries_4);
        suggestion4.startAnimation(fadeIn);

        suggestion5.setText(entries_5);
        suggestion5.startAnimation(fadeIn);

        // Get parameters



        if (entries_1.equals("")){
            suggestion1.setVisibility(View.GONE);
            suggestion1.startAnimation(fadeOut);
        }
        if (!entries_1.equals("")){
            suggestion1.setVisibility(View.VISIBLE);
            suggestion1.startAnimation(fadeIn);
            if (suggestion1.getVisibility() == View.VISIBLE){
                no_parameters.setVisibility(View.INVISIBLE);
                no_parameters.setAnimation(fadeOut);
            }
        }
        if (entries_2.equals("")){
            suggestion2.setVisibility(View.GONE);
            suggestion2.startAnimation(fadeOut);
        }
        if (!entries_2.equals("")){
            suggestion2.setVisibility(View.VISIBLE);
            suggestion2.startAnimation(fadeIn);
            if (suggestion2.getVisibility() == View.VISIBLE){
                no_parameters.setVisibility(View.INVISIBLE);
                no_parameters.setAnimation(fadeOut);
            }
        }
        if (entries_3.equals("")){
            suggestion3.setVisibility(View.GONE);
            suggestion3.startAnimation(fadeOut);
        }
        if (!entries_3.equals("")){
            suggestion3.setVisibility(View.VISIBLE);
            suggestion3.startAnimation(fadeIn);
            if (suggestion3.getVisibility() == View.VISIBLE){
                no_parameters.setVisibility(View.INVISIBLE);
                no_parameters.setAnimation(fadeOut);
            }
        }
        if (entries_4.equals("")){
            suggestion4.setVisibility(View.GONE);
            suggestion4.startAnimation(fadeOut);
        }
        if (!entries_4.equals("")){
            suggestion4.setVisibility(View.VISIBLE);
            suggestion4.startAnimation(fadeIn);
            if (suggestion4.getVisibility() == View.VISIBLE){
                no_parameters.setVisibility(View.INVISIBLE);
                no_parameters.setAnimation(fadeOut);
            }
        }
        if (entries_5.equals("")){
            suggestion5.setVisibility(View.GONE);
            suggestion5.startAnimation(fadeOut);
        }
        if (!entries_5.equals("")){
            suggestion5.setVisibility(View.VISIBLE);
            suggestion5.startAnimation(fadeIn);
            if (suggestion5.getVisibility() == View.VISIBLE){
                no_parameters.setVisibility(View.INVISIBLE);
                no_parameters.setAnimation(fadeOut);
            }
        }
        if (entries_1.equals("") && entries_2.equals("") && entries_3.equals("") && entries_4.equals("") && entries_5.equals("")){
            suggestion1.setVisibility(View.GONE);
            suggestion1.startAnimation(fadeOut);
            suggestion2.setVisibility(View.GONE);
            suggestion2.startAnimation(fadeOut);
            suggestion3.setVisibility(View.GONE);
            suggestion3.startAnimation(fadeOut);
            suggestion4.setVisibility(View.GONE);
            suggestion4.startAnimation(fadeOut);
            suggestion5.setVisibility(View.GONE);
            suggestion5.startAnimation(fadeOut);

            if (suggestion1.getVisibility() == View.GONE){
                no_parameters.setVisibility(View.VISIBLE);
                no_parameters.setAnimation(fadeIn);
            }

        }
        if (!entries_1.equals("") && !entries_2.equals("") && !entries_3.equals("") && !entries_4.equals("") && !entries_5.equals("")){
            suggestion1.setVisibility(View.VISIBLE);
            suggestion1.startAnimation(fadeIn);
            suggestion2.setVisibility(View.VISIBLE);
            suggestion2.startAnimation(fadeIn);
            suggestion3.setVisibility(View.VISIBLE);
            suggestion3.startAnimation(fadeIn);
            suggestion4.setVisibility(View.VISIBLE);
            suggestion4.startAnimation(fadeIn);
            suggestion5.setVisibility(View.VISIBLE);
            suggestion5.startAnimation(fadeIn);
            if (suggestion1.getVisibility() == View.VISIBLE){
                no_parameters.setVisibility(View.INVISIBLE);
                no_parameters.setAnimation(fadeOut);
            }
        }
    }

    @Override
    public void onError(AIError error) {
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
    public void back(View view){
        Intent back = new Intent(Assistant.this,Home.class);
        startActivity(back);
        Assistant.this.finish();
    }
}
