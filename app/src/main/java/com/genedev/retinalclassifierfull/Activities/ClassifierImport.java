package com.genedev.retinalclassifierfull.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.AssistantHelpers.Assistant;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wonderkiln.camerakit.CameraView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.view.View.VISIBLE;

public class ClassifierImport extends AppCompatActivity {

    // Model initialization, image size (224x224), image mean, image std, input name, output name.
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";

    // Setting the path of the model (graph.pb) and label (labels.txt).
    private static final String MODEL_FILE = "file:///android_asset/mobilenets_retrained_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/mobilenets_retrained_labels.txt";

    // Variable declaration.
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult, textView,resultinfo;
    private ImageButton btnRefresh,btnExport,buttonLoadImage;
    private ImageView imageViewResult;
    private CameraView cameraView;
    private ImageView imageView;
    private LottieAnimationView btnDetectObject;
    private FloatingActionButton fab;
    private ImageView imageAnalyze;
    private int RESULT_LOAD_IMG;
    private String resultsFinal;
    private TextView instruction;

    private static int TIME_OUT = 1000;

    private TextView text;
    final Context context =this;
    private Bitmap selectedImage;

    private Animation fadeIn1;
    private AnimationSet animation;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;

    private CardView card_assistant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier_import);

        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        imageAnalyze =findViewById(R.id.imgAnalyze);
        imageView = findViewById(R.id.loadedImg);
        fab = findViewById(R.id.loadImage);
        resultinfo = findViewById(R.id.resultinfo);
        imageView = findViewById(R.id.loadedImg);
        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);
        textViewResult = findViewById(R.id.textViewResult);
        textView = findViewById(R.id.textView);
        instruction = findViewById(R.id.instruction);

        card_assistant = findViewById(R.id.card_assistant);

        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        alertDialog();

        fadeIn1 = new AlphaAnimation(0, 1);
        fadeIn1.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn1.setDuration(1000);

        checkDrawable();
        initTensorFlowAndLoadModel();

        card_assistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assitant = new Intent(ClassifierImport.this, Assistant.class);
                startActivity(assitant);
            }
        });
    }

    public void load(View v){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    public void analyze(View v){
        alertDialogAnalyzing();
    }

    private void checkDrawable(){
        //Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        boolean hasDrawable = (imageView.getDrawable() != null);

        if(imageView.getDrawable() != null){
            imageAnalyze.setVisibility(VISIBLE);
            instruction.setText("Click the the button in the center of the screen to analyze the image.");
        }
        else{
            imageAnalyze.setVisibility(View.INVISIBLE);
            instruction.setText("Import a fundus image from your gallery.");
        }
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
            }
        }, TIME_OUT);

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
                        Intent save = new Intent(ClassifierImport.this, PatientAddInfoNew.class);
                        save.putExtra("result", resultsFinal);
                        save.putExtra("classifyType", "Fundus Import");
                        startActivity(save);
                        ClassifierImport.this.finish();
                    }
                }, TIME_OUT);
            }
        });

        dialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassifierImport.this,ClassificationModes.class);
                startActivity(intent);
                ClassifierImport.this.finish();
            }
        });

        dialog.show();
    }

    private void alertDialogAnalyzing(){
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

                                                imageView.setImageBitmap(selectedImage);
                                                imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                                selectedImage = Bitmap.createScaledBitmap(selectedImage, INPUT_SIZE, INPUT_SIZE, false);

                                                final List<Classifier.Recognition> results = classifier.recognizeImage(selectedImage);
                                                textViewResult.setVisibility(VISIBLE);
                                                text.startAnimation(fadeIn1);
                                                textViewResult.setText(results.toString());
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
                                                String action = "New Analysis (Retinal Import)";

                                                writeToHistory(id,action,date,time);

                                                imageAnalyze.setVisibility(View.INVISIBLE);
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
    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }

    public void retry(View v){
        recreate();
    }

    public void save(View v){
        if (Objects.equals(textViewResult.getText().toString(), "")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ClassifierImport.this);
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

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                checkDrawable();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ClassifierImport.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(ClassifierImport.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure, you want to go back and select another retinal image?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent back = new Intent(ClassifierImport.this,ClassificationModes.class);
                                startActivity(back);
                                ClassifierImport.this.finish();
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
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, you want to go back and select another retinal image?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent back = new Intent(ClassifierImport.this,ClassificationModes.class);
                        startActivity(back);
                        ClassifierImport.this.finish();
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
                            Intent back = new Intent(ClassifierImport.this,ClassificationModes.class);
                            startActivity(back);
                            ClassifierImport.this.finish();
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
        if (keyCode == KeyEvent.KEYCODE_HOME)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, you want to go back?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            ClassifierImport.this.finish();
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
