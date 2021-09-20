package com.example.a303com_laukuansin.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.DigitsKeyListener;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.CropImageActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.example.a303com_laukuansin.utilities.QuantityValueFilter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class EditPersonalInformationFragment extends BaseFragment implements ActivityLevelDialogFragment.SelectActivityDialogListener {
    private User user;
    private TextInputLayout _inputName,_inputEmail,_inputAge,_inputActivity,_inputHeight,_inputCurrentWeight,_inputTargetWeight,_inputBMI;
    private String gender;
    private TextInputEditText _inputEditName,_inputEditEmail,_inputEditAge,_inputEditActivity,_inputEditHeight,_inputEditStartWeight,_inputEditCurrentWeight,_inputEditTargetWeight,_inputEditBMI;
    private LinearLayout _maleLayout,_femaleLayout;
    private TextView _maleText,_femaleText;
    private double activityLevel;
    private CircleImageView _profileImage;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int GALLERY_PERMISSION_CODE = 102;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 300;
    private File imageFile;
    private SweetAlertDialog _progressDialog;


    public EditPersonalInformationFragment() {
        user = getSessionHandler().getUser();
    }

    public static EditPersonalInformationFragment newInstance() {
        return new EditPersonalInformationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_personal_information, container, false);
        initialization(view);
        //load data
        loadData();
        return view;
    }

    private void initialization(View view)
    {
        //bind view with id
        _inputName = view.findViewById(R.id.nameLayout);
        _inputEmail = view.findViewById(R.id.emailLayout);
        _inputAge = view.findViewById(R.id.ageLayout);
        _inputActivity = view.findViewById(R.id.activeLayout);
        _inputHeight = view.findViewById(R.id.heightLayout);
        _inputCurrentWeight = view.findViewById(R.id.currentWeightLayout);
        _inputTargetWeight = view.findViewById(R.id.targetWeightLayout);
        _inputBMI = view.findViewById(R.id.bmiLayout);
        _inputEditName = view.findViewById(R.id.editTextName);
        _inputEditEmail = view.findViewById(R.id.editTextEmail);
        _inputEditAge = view.findViewById(R.id.editTextAge);
        _inputEditActivity = view.findViewById(R.id.editTextActivity);
        _inputEditHeight = view.findViewById(R.id.editTextHeight);
        _inputEditStartWeight = view.findViewById(R.id.editTextStartWeight);
        _inputEditCurrentWeight = view.findViewById(R.id.editTextCurrentWeight);
        _inputEditTargetWeight = view.findViewById(R.id.editTextTargetWeight);
        _inputEditBMI = view.findViewById(R.id.editTextBMI);
        _maleLayout = view.findViewById(R.id.maleLayout);
        _femaleLayout = view.findViewById(R.id.femaleLayout);
        _maleText = view.findViewById(R.id.maleText);
        _femaleText = view.findViewById(R.id.femaleText);
        Button _updateButton = view.findViewById(R.id.updateButton);
        _profileImage = view.findViewById(R.id.profileImage);

        //initialize progress dialog
        _progressDialog = showProgressDialog("Loading..",getResources().getColor(R.color.colorPrimary));

        //when click profile image
        _profileImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                createProfileImageBottomDialog();
            }
        });

        //when click male
        _maleLayout.setOnClickListener(v -> {
            _maleLayout.setBackgroundColor(getResources().getColor(R.color.blue_300));
            _maleText.setTextColor(getResources().getColor(R.color.white_1000));
            _femaleLayout.setBackgroundColor(getResources().getColor(R.color.grey_50));
            _femaleText.setTextColor(getResources().getColor(R.color.grey_600));
            gender = "Male";
        });

        //when click female
        _femaleLayout.setOnClickListener(v -> {
            _maleLayout.setBackgroundColor(getResources().getColor(R.color.grey_50));
            _maleText.setTextColor(getResources().getColor(R.color.grey_600));
            _femaleLayout.setBackgroundColor(getResources().getColor(R.color.red_A100));
            _femaleText.setTextColor(getResources().getColor(R.color.white_1000));
            gender = "Female";
        });

        //set the key on can enter 0 to 9
        _inputEditAge.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        //set filter for the age can only type 2 number without decimal and min value is 15 and max value is 99
        _inputEditAge.setFilters(new InputFilter[]{new QuantityValueFilter(2, 0, 99,15)});
        //set the key on can enter 0 to 9
        _inputEditHeight.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        //set filter for the height can only type 3 number without decimal and min value is 100 and max value is 270
        _inputEditHeight.setFilters(new InputFilter[]{new QuantityValueFilter(3, 0, 270,100)});

        //set filter for the current weight can only type maximum 3 number and max value is 300.9, the reason to put min as 0 is because if put min as 30, then user cannot enter 100~299
        _inputEditCurrentWeight.setFilters(new InputFilter[]{new QuantityValueFilter(3, 1, 300.9,0)});
        //set filter for the target weight can only type maximum 3 number and max value is 300.9, the reason to put min as 0 is because if put min as 30, then user cannot enter 100~299
        _inputEditTargetWeight.setFilters(new InputFilter[]{new QuantityValueFilter(3, 1, 300.9,0)});

        //when click daily active
        _inputEditActivity.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ActivityLevelDialogFragment fragment = ActivityLevelDialogFragment.newInstance(EditPersonalInformationFragment.this);
                fragment.show(fragmentManager,"");
            }
        });

        //when click bmi
        _inputEditBMI.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                createBMIBottomDialog();
            }
        });
    }

    private void loadData()
    {
        //if user name is not empty
        if(!user.getName().isEmpty())
        {
            //set name
            _inputEditName.setText(user.getName());
        }
        //if user email is not empty
        if(!user.getEmailAddress().isEmpty())
        {
            //set email
            _inputEditEmail.setText(user.getEmailAddress());
        }
        //if user gender is not empty
        if(!user.getGender().isEmpty())
        {
            gender = user.getGender();
            if(gender.equals("Male"))
            {
                _maleLayout.setBackgroundColor(getResources().getColor(R.color.blue_300));
                _maleText.setTextColor(getResources().getColor(R.color.white_1000));
            }
            else if(gender.equals("Female"))
            {
                _femaleLayout.setBackgroundColor(getResources().getColor(R.color.red_A100));
                _femaleText.setTextColor(getResources().getColor(R.color.white_1000));
            }
        }
        //if user age is not equal to 0
        if(user.getAge()!=0)
        {
            //set the age
            _inputEditAge.setText(String.valueOf(user.getAge()));
        }
        //if user activity is not empty
        if(user.getActivityLevel()!=0)
        {
            activityLevel = user.getActivityLevel();
            setActivityText(activityLevel);
        }
        //if user height is not equal to 0
        if(user.getHeight()!=0)
        {
            //set height
            _inputEditHeight.setText(String.valueOf(user.getHeight()));
        }
        //if the user starting weight is not equal to 0
        if(user.getStartWeight()!=0)
        {
            //set starting weight
            _inputEditStartWeight.setText(String.format("%.1f KG on %2$s",user.getStartWeight(),user.getDateCreated()));
        }
        //if the user current weight is not equal to 0
        if(user.getWeight()!=0)
        {
            //set current weight
            _inputEditCurrentWeight.setText(String.valueOf(user.getWeight()));
        }
        //if the user target weight is not equal to 0
        if(user.getTargetWeight()!=0)
        {
            //set target weight
            _inputEditTargetWeight.setText(String.valueOf(user.getTargetWeight()));
        }
        //if the user BMI is not equal to 0
        if(user.getBMI()!=0)
        {
            //set bmi
            _inputEditBMI.setText(String.valueOf(user.getBMI()));
        }
    }

    private void setActivityText(double activityLevel)
    {
        if(activityLevel==1.2)
        {
            _inputEditActivity.setText("Sedentary");
        }
        else if(activityLevel==1.375)
        {
            _inputEditActivity.setText("Lightly Active");
        }
        else if(activityLevel==1.55)
        {
            _inputEditActivity.setText("Moderately Active");
        }
        else if(activityLevel==1.725)
        {
            _inputEditActivity.setText("Very Active");
        }
    }

    private void createProfileImageBottomDialog()
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_profile_image);
        LinearLayout _cameraLayout = bottomSheetDialog.findViewById(R.id.cameraLayout);
        LinearLayout _galleryLayout = bottomSheetDialog.findViewById(R.id.galleryLayout);
        //when click camera
        _cameraLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //check the camera permission
                if (checkCameraPermission()) {
                    snapImageFromCamera();
                } else {
                    requestCameraPermission();
                }
                bottomSheetDialog.cancel();
            }
        });
        //when click gallery
        _galleryLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //check the storage permission
                if (checkStoragePermission()) {
                    selectImageFromGallery();
                } else {
                    requestStoragePermission();
                }
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean isCameraPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean isWriteExternalPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return isCameraPermission && isWriteExternalPermission;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
    }

    //checking storage permissions
    private Boolean checkStoragePermission() {
        boolean isReadExternalPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        boolean isWriteExternalPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return isReadExternalPermission && isWriteExternalPermission;
    }

    // Requesting camera permission
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //permission call back
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean cameraGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (cameraGranted && writeStorageGranted) {
                    snapImageFromCamera();
                } else {
                    Toast.makeText(getContext(), "Please Enable Camera and Storage Permissions before take image", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean readStorageGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (readStorageGranted && writeStorageGranted) {
                    selectImageFromGallery();
                } else {
                    Toast.makeText(getContext(), "Please Enable Storage Permissions before select image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void snapImageFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            imageFile = createImageFile();
            // Continue only if the File was successfully created
            if (imageFile != null) {
                Uri uri = FileProvider.getUriForFile(getContext(), "com.example.a303com_laukuansin.fileProvider", imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        } catch (Exception e) {
            // Error occurred while creating the File
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = Calendar.getInstance().getTimeInMillis() + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Uri uri = Uri.fromFile(imageFile);
                    //set the image
                    Picasso.get().load(uri).placeholder(R.drawable.ic_image_holder).into(_profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (_progressDialog.isShowing())
                                _progressDialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            if (_progressDialog.isShowing())
                                _progressDialog.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            }
            case GALLERY_REQUEST_CODE: {
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    //create a new file and write the input stream from uri to new file
                    writeUriToNewFile(uri);
                    //set the image
                    Picasso.get().load(uri).placeholder(R.drawable.ic_image_holder).into(_profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (_progressDialog.isShowing())
                                _progressDialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            if (_progressDialog.isShowing())
                                _progressDialog.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            }
        }
    }

    private void writeUriToNewFile(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            imageFile = createImageFile();
            try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createBMIBottomDialog()
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_bmi);
        TextView _BMIView = bottomSheetDialog.findViewById(R.id.dialogBMIView);

        String category="";
        double BMI = user.getBMI();
        if(BMI<18.5)//if BMI less than 18.5
        {
            category = "Underweight";
        }
        else if(BMI<25)//if BMI in between 18.5 to 24.9
        {
            category = "Ideal";
        }
        else if(BMI<30)//if BMI in between 25 to 29.9
        {
            category = "Overweight";
        }
        else// else BMI is bigger or equal to 30
        {
            category = "Obesity";
        }
        String BMIText= String.format("According to National Institutes of Health, your BMI is %.1f, which is consider as %2$s",BMI,category);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                //link to the NIH article
                String url = "https://www.nih.gov/news-events/news-releases/nih-study-identifies-ideal-body-mass-index";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        };
        SpannableString spannableString = new SpannableString(BMIText);
        spannableString.setSpan(clickableSpan, 0,42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        _BMIView.setText(spannableString);
        _BMIView.setMovementMethod(LinkMovementMethod.getInstance());
        _BMIView.setHighlightColor(Color.TRANSPARENT);

        bottomSheetDialog.show();
    }


    @Override
    public void onReturnActivityLevel(double activityLevel) {
        this.activityLevel = activityLevel;
        setActivityText(activityLevel);
    }
}
