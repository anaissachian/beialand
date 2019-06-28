package com.example.solomon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solomon.networkPackets.ImageData;
import com.example.solomon.networkPackets.UpdateUserData;
import com.example.solomon.runnables.SendImageRunable;
import com.example.solomon.runnables.SendUserUpdateData;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ProfileSettingsActivity extends AppCompatActivity {

    //UI variables
    private ImageView backButton;
    public static CircularImageView profilePicture;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private TextView ageTextView;
    private ImageView usernameEditButton;//imageview used like a button
    private ImageView passwordEditButton;//imageview used like a button
    private ImageView ageEditButton;//imageview used like a button
    private Button cancelUsernameChangesButton;
    private Button cancelPasswordChangesButton;
    private Button cancelAgeChangesButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText ageEditText;
    private Button saveChangesButton;

    //intent variables
    public int GALLERY_REQUEST_CODE = 0;

    //other variables
    public static Context profileSettingsContext;
    public static ProfileSettingsActivity profileSettingsActivity;

    //handlers
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    //change the profile picture
                    ImageData imageData = (ImageData) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData.getImageBytes(), 0, imageData.getImageBytes().length);
                    ProfileSettingsActivity.profilePicture.setImageBitmap(bitmap);
                    Toast.makeText(ProfileSettingsActivity.profileSettingsContext, "updated the profile picture", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        profileSettingsContext = getApplicationContext();
        profileSettingsActivity = this;
        initUI();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        usernameEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveChangesButton.getVisibility() != View.VISIBLE)
                    saveChangesButton.setVisibility(View.VISIBLE);
                usernameTextView.setVisibility(View.GONE);
                usernameEditText.setVisibility(View.VISIBLE);
                usernameEditButton.setVisibility(View.GONE);
                cancelUsernameChangesButton.setVisibility(View.VISIBLE);
            }
        });

        passwordEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveChangesButton.getVisibility() != View.VISIBLE)
                    saveChangesButton.setVisibility(View.VISIBLE);
                passwordTextView.setVisibility(View.GONE);
                passwordEditText.setVisibility(View.VISIBLE);
                passwordEditButton.setVisibility(View.GONE);
                cancelPasswordChangesButton.setVisibility(View.VISIBLE);
            }
        });

        ageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveChangesButton.getVisibility() != View.VISIBLE)
                    saveChangesButton.setVisibility(View.VISIBLE);
                ageTextView.setVisibility(View.GONE);
                ageEditText.setVisibility(View.VISIBLE);
                ageEditButton.setVisibility(View.GONE);
                cancelAgeChangesButton.setVisibility(View.VISIBLE);
            }
        });

        cancelUsernameChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordEditButton.getVisibility() == View.VISIBLE && ageEditButton.getVisibility() == View.VISIBLE)
                {
                    saveChangesButton.setVisibility(View.GONE);
                }
                usernameEditText.setVisibility(View.GONE);
                usernameTextView.setVisibility(View.VISIBLE);
                cancelUsernameChangesButton.setVisibility(View.GONE);
                usernameEditButton.setVisibility(View.VISIBLE);
            }
        });

        cancelPasswordChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditButton.getVisibility() == View.VISIBLE && ageEditButton.getVisibility() == View.VISIBLE)
                {
                    saveChangesButton.setVisibility(View.GONE);
                }
                passwordEditText.setVisibility(View.GONE);
                passwordTextView.setVisibility(View.VISIBLE);
                cancelPasswordChangesButton.setVisibility(View.GONE);
                passwordEditButton.setVisibility(View.VISIBLE);
            }
        });

        cancelAgeChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditButton.getVisibility() == View.VISIBLE && passwordEditButton.getVisibility() == View.VISIBLE)
                {
                    saveChangesButton.setVisibility(View.GONE);
                }
                ageEditText.setVisibility(View.GONE);
                ageTextView.setVisibility(View.VISIBLE);
                cancelAgeChangesButton.setVisibility(View.GONE);
                ageEditButton.setVisibility(View.VISIBLE);
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //get the data from the UI
                String username = null, password = null;
                int age = 0;
                if(usernameEditText.getVisibility() == View.VISIBLE)
                {
                    username = usernameEditText.getText().toString().trim();
                    if(username.equals("")) {
                        username = null;
                    }
                }
                if(passwordEditText.getVisibility() == View.VISIBLE)
                {
                    password = passwordEditText.getText().toString().trim();
                    if(password.equals(""))
                        password = null;
                }
                if(ageEditText.getVisibility() == View.VISIBLE)
                {
                    try
                    {
                        age = Integer.parseInt(ageEditText.getText().toString().trim());
                        if(age < 0) {
                            age = 0;
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        age = 0;
                    }
                }

                //send update data to the server if at least one field was modified
                if(username != null || password != null || age != 0) {
                    UpdateUserData updateUserData = new UpdateUserData(MainActivity.userId, username, password, age);
                    Thread updateUserDataThread = new Thread(new SendUserUpdateData(MainActivity.objectOutputStream, updateUserData));
                    updateUserDataThread.start();
                    Toast.makeText(ProfileSettingsActivity.profileSettingsContext, "info updated", Toast.LENGTH_LONG).show();
                }

                //change the user data
                if(username != null) {
                    usernameTextView.setText(MainActivity.username);
                    MainActivity.username = username;
                    usernameTextView.setText(username);
                }
                if(age != 0) {
                    ageTextView.setText(Integer.toString(MainActivity.age));
                    MainActivity.age = age;
                    ageTextView.setText(Integer.toString(age));
                }

                //set the UI as it was before updating
                cancelUsernameChangesButton.setVisibility(View.GONE);
                cancelPasswordChangesButton.setVisibility(View.GONE);
                cancelAgeChangesButton.setVisibility(View.GONE);
                usernameEditText.setVisibility(View.GONE);
                passwordEditText.setVisibility(View.GONE);
                ageEditText.setVisibility(View.GONE);
                usernameEditText.setText("");
                passwordEditText.setText("");
                ageEditText.setText("");
                usernameEditButton.setVisibility(View.VISIBLE);
                passwordEditButton.setVisibility(View.VISIBLE);
                ageEditButton.setVisibility(View.VISIBLE);
                usernameTextView.setVisibility(View.VISIBLE);
                passwordTextView.setVisibility(View.VISIBLE);
                ageTextView.setVisibility(View.VISIBLE);
                saveChangesButton.setVisibility(View.GONE);
            }
        });

    }
    public void initUI()
    {
        backButton = findViewById(R.id.profileSettingsBackButton);
        profilePicture = findViewById(R.id.profilePicture);
        nameTextView = findViewById(R.id.nameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        passwordTextView = findViewById(R.id.passwordTexView);
        ageTextView = findViewById(R.id.ageTextView);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ageEditText = findViewById(R.id.ageEditText);
        usernameEditButton = findViewById(R.id.editUsernameButton);
        passwordEditButton = findViewById(R.id.editPasswordButton);
        ageEditButton = findViewById(R.id.editAgeButton);
        cancelUsernameChangesButton = findViewById(R.id.cancelUsernameChangeButton);
        cancelPasswordChangesButton = findViewById(R.id.cancelPasswordChangeButton);
        cancelAgeChangesButton = findViewById(R.id.cancelAgeChangeButton);
        saveChangesButton = findViewById(R.id.saveChangesButton);


        //set the UI based on user data
        nameTextView.setText(MainActivity.lastName + " " + MainActivity.firstName);
        usernameTextView.setText(MainActivity.username);
        ageTextView.setText(Integer.toString(MainActivity.age));
        //get the image from the server
        Thread requestProfilePictureThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    synchronized (MainActivity.objectOutputStream) {
                        //send the photo request
                        MainActivity.objectOutputStream.writeObject("Photo request");
                    }
                    synchronized (MainActivity.objectInputStream) {
                        //receive the image data
                        ImageData imageData = (ImageData) MainActivity.objectInputStream.readObject();
                        //Send a message to the ProfileSettings activity handler with the imagedata so we can change the profile picture
                        Message message = Message.obtain(ProfileSettingsActivity.handler);
                        message.what = 1;
                        message.obj = imageData;
                        message.sendToTarget();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        requestProfilePictureThread.start();

    }


    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case 0://GALLERY_REQUEST_CODE
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImageUri = data.getData();
                    try
                    {
                        //extract bytes from the imageUri and create an object that contains the profile picture and the user id
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //compress the bitmap data and save it into a byte array
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageContent = baos.toByteArray();
                        int userId = MainActivity.userId;
                        ImageData imageData = new ImageData(imageContent, userId);
                        baos.close();

                        //set the profile picture with the selected one
                        profilePicture.setImageURI(selectedImageUri);
                        Toast.makeText(this, "Updated the profile picture", Toast.LENGTH_LONG).show();

                        //start sending image thread
                        synchronized (MainActivity.objectOutputStream)
                        {
                            Thread sendImageThread = new Thread(new SendImageRunable(imageData, MainActivity.objectOutputStream));
                            sendImageThread.start();
                        }
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(this, "Image format error", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

            }
        }
    }
}
