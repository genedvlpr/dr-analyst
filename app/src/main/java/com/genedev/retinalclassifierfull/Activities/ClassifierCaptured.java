package com.genedev.retinalclassifierfull.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.AssistantHelpers.Assistant;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.view.View.VISIBLE;

public class ClassifierCaptured extends AppCompatActivity {

    // Model initialization, image size (224x224), image mean, image std, input name, output name.
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";

    // Setting the path of the model (graph.pb) and label (labels.txt).
    private static final String MODEL_FILE = "file:///android_asset/mobilenets_retrained_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/mobilenets_retrained_labels.txt";

    // Variable declaration.
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult, textView;
    private ImageView imageViewResult, imgAnalyze;
    private CameraView cameraView;
    private LottieAnimationView btnDetectObject;
    private ProgressDialog progressDialog;
    private Dialog progressDialog1;

    private ImageButton flash, focus, adjust, front_facing, rear, neg2, neg1, zero, plus1, plus2;

    private static int TIME_OUT = 1000;
    private String resultsFinal;

    final Context context = this;
    private DatabaseReference mDatabase;
    private TextView text;
    private CameraKitImage cameraKitImage;
    private Animation fadeIn1,fadeIn2,fadeIn3,fadeIn4,fadeIn5;
    private AnimationSet animation;
    private Button btn_yes,btn_discard;
    private Bitmap bitmap;
    private TextView instruction;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;

    private CardView card_assistant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier_captured_new);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);
        textViewResult = findViewById(R.id.textViewResult);
        textView = findViewById(R.id.textView);
        instruction = findViewById(R.id.instruction);
        imgAnalyze = findViewById(R.id.imgAnalyze);
        flash = findViewById(R.id.flash);
        adjust = findViewById(R.id.adjust_flash);
        front_facing = findViewById(R.id.facing);
        rear = findViewById(R.id.rear);
        neg2 = findViewById(R.id.neg_2);
        neg1 = findViewById(R.id.neg_1);
        zero = findViewById(R.id.neg_0);
        plus1 = findViewById(R.id.plus_1);
        plus2 = findViewById(R.id.plus_2);

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.getFlash();
                cameraView.setFlash(3);
            }
        });

        adjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.getFlash();
                cameraView.setFlash(1);
            }
        });

        front_facing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.setFacing(1);
            }
        });

        rear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.setFacing(2);
            }
        });

        neg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom(1);
            }
        });

        neg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom(2);
            }
        });

        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom(3);
            }
        });

        plus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom(4);
            }
        });

        plus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom(5);
            }
        });
        //btnDetectObject = findViewById(R.id.btnDetectObject);

        card_assistant = findViewById(R.id.card_assistant);

        progressDialog1 = new ProgressDialog(ClassifierCaptured.this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        fadeIn1 = new AlphaAnimation(0, 1);
        fadeIn1.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn1.setDuration(1000);

        alertDialog();

        textViewResult.setMovementMethod(new ScrollingMovementMethod());


        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
            }
            @Override
            public void onError(CameraKitError cameraKitError) {
            }
            @Override
            public void onImage(final CameraKitImage cameraKitImage) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_container_analyzing);
                dialog.setCancelable(false);

                text = dialog.findViewById(R.id.headline);

                animation = new AnimationSet(false);
                animation.addAnimation(fadeIn1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        text.startAnimation(fadeIn1);
                        text.setText("Reading Tensorflow Model.");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                text.startAnimation(fadeIn1);
                                text.setText("Defining image size.");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        text.startAnimation(fadeIn1);
                                        text.setText("Analyzing image data.");
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                text.startAnimation(fadeIn1);
                                                text.setText("Finalyzing diagnostic and result.");
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        text.startAnimation(fadeIn1);
                                                        text.setText("Finalizing diagnosis and result.");

                                                        Bitmap bitmap = cameraKitImage.getBitmap();

                                                        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                                                        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

                                                        textViewResult.setText(results.toString());

                                                        text.startAnimation(fadeIn1);
                                                        textViewResult.setText(results.toString());

                                                        textView.setVisibility(VISIBLE);
                                                        textViewResult.setVisibility(VISIBLE);


                                                        dialog.dismiss();
                                                        instruction.setVisibility(View.INVISIBLE);

                                                        //get date and time for history
                                                        currentTime = Calendar.getInstance().getTime();

                                                        time_date = String.valueOf(currentTime);

                                                        calendar = Calendar.getInstance();
                                                        year = calendar.get(Calendar.YEAR);
                                                        month = calendar.get(Calendar.MONTH);
                                                        day = calendar.get(Calendar.DAY_OF_MONTH);

                                                        year_date = String.valueOf(year);
                                                        month_date = String.valueOf(month);
                                                        day_date = String.valueOf(day);
                                                        String id = mAuth.getUid();
                                                        //Set the value of date and time and  push to Firebase Database.

                                                        String date = (month_date + "/" + day_date + "/" + year_date);
                                                        String time = time_date;
                                                        String action = "New Analysis (Retinal Captured)";

                                                        writeToHistory(id,action,date,time);

                                                        imgAnalyze.setVisibility(View.INVISIBLE);
                                                    }
                                                }, TIME_OUT);
                                            }
                                        }, TIME_OUT);
                                    }
                                }, TIME_OUT);
                            }
                        }, TIME_OUT);
                    }
                }, TIME_OUT);

                dialog.show();


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
        initTensorFlowAndLoadModel();

        Button save = findViewById(R.id.savebtn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(textViewResult.getText().toString(), "")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ClassifierCaptured.this);
                    alertDialogBuilder.setMessage("There's no diagnosis, try again.");
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialogBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.hide();
                        }
                    });
                    alertDialog.show();
                }

                if (!Objects.equals(textViewResult.getText().toString(), "")) {
                    alertDialogSave();
                }
            }
        });


        card_assistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assitant = new Intent(ClassifierCaptured.this, Assistant.class);
                startActivity(assitant);
            }
        });
    }

    private void zoom(int fl){
        cameraView.setZoom(fl);
    }

    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    // Initialize and load tensorflow model.
    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    public void analyze(View v){
        cameraView.captureImage();
    }

    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container_analyzing);

        TextView text = (TextView) dialog.findViewById(R.id.headline);
        text.setText("Preparing Tensorflow Model.");
        ImageView image = (ImageView) dialog.findViewById(R.id.img_warn);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                alertDialogLens();
            }
        }, TIME_OUT);

        dialog.show();
    }
    private void alertDialogLens(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container_capture);

        TextView text = (TextView) dialog.findViewById(R.id.headline);
        ImageView image = (ImageView) dialog.findViewById(R.id.img_warn);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_btn);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
    private void alertDialogSave(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container_save);

        TextView text = (TextView) dialog.findViewById(R.id.headline);
        text.setText("Are you sure you want to save this diagnosis or discard it?");
        ImageView image = (ImageView) dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_btn);
        Button dialogButton1 = (Button) dialog.findViewById(R.id.dialog_btn_cancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_container_analyzing);
                TextView text = (TextView) dialog.findViewById(R.id.headline);
                text.setText("Finalizing diagnosis.");
                resultsFinal = textViewResult.getText().toString();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent save = new Intent(ClassifierCaptured.this, PatientAddInfoNew.class);
                        save.putExtra("result", resultsFinal);
                        save.putExtra("classifyType", "Fundus Capture");
                        startActivity(save);
                        ClassifierCaptured.this.finish();
                    }
                }, TIME_OUT);
            }
        });

        dialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassifierCaptured.this,ClassificationModes.class);
                startActivity(intent);
                ClassifierCaptured.this.finish();
            }
        });

        dialog.show();
    }



    public void retry(View v){
        recreate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, you want to go back?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent back = new Intent(ClassifierCaptured.this,ClassificationModes.class);
                        startActivity(back);
                        ClassifierCaptured.this.finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, you want to go back?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent back = new Intent(ClassifierCaptured.this,ClassificationModes.class);
                            startActivity(back);
                            ClassifierCaptured.this.finish();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onKeyDown(keyCode, event);
    }

}
