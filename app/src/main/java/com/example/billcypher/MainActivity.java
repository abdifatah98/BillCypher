package com.example.billcypher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button captureImageBtn, detectTextBtn;
    private ImageView imageView;
    private TextView textView;
    private Uri mPhotoUri;

    ArrayList<Double> prices = new ArrayList<Double>();
    ArrayList<String> items = new ArrayList<String>();

    final int PIC_CROP = 3;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageBtn = findViewById(R.id.capture_image_btn);
        detectTextBtn = findViewById(R.id.detect_text_image_btn);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_display);

        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textView.setText("");
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //imageView.setImageURI(mPhotoUri);
            CropImage.activity(mPhotoUri)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mPhotoUri = result.getUri();
                imageView.setImageURI(mPhotoUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void detectTextFromImage() {
        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(getApplicationContext(), mPhotoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result = detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText result) {
                displayTextFromImage(result);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                Log.d("Error: ", Objects.requireNonNull(e.getMessage()));
            }
        });

    }

    private void displayTextFromImage(FirebaseVisionText result) {
        String resultText = result.getText();
        int blockNumber = 0;
        if (resultText.length() == 0) {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (FirebaseVisionText.TextBlock block : result.getTextBlocks()) {
                Rect blockFrame = block.getBoundingBox();
                if(blockFrame.width() < 500){
                    for (FirebaseVisionText.Line line: block.getLines()) {
                        Pattern pattern = Pattern.compile("\\d+(?:\\.\\d+)?");
                        Matcher matcher = pattern.matcher(line.getText());
                        if (matcher.find()) {
                            prices.add(Double.parseDouble(matcher.group(0)));
                        }
                    }
                }
                else{
                    for (FirebaseVisionText.Line line: block.getLines()){
                        items.add(line.getText());
                    }
                }
            }
            Intent GoToItems = new Intent(getApplicationContext(), ItemsActivity.class);
            GoToItems.putStringArrayListExtra("Items", items);
            GoToItems.putExtra("Price", prices);
            startActivity(GoToItems);
        }
    }
}