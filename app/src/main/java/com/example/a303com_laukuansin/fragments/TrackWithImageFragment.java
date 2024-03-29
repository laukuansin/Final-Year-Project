package com.example.a303com_laukuansin.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.CropImageActivity;
import com.example.a303com_laukuansin.activities.MealDetailActivity;
import com.example.a303com_laukuansin.activities.TrackWithImageActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.FoodClass;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.requests.MealDetailRequest;
import com.example.a303com_laukuansin.responses.FoodClassifyResponse;
import com.example.a303com_laukuansin.responses.MealDetailResponse;
import com.example.a303com_laukuansin.utilities.ApiClient;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class TrackWithImageFragment extends BaseFragment {
    private String date;
    private String mealType;
    private final User user;
    private ImageView _imageView;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int GALLERY_PERMISSION_CODE = 102;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 300;
    public static final int CROP_IMAGE_RESULT = 400;
    private UploadImageToStorage _uploadImage = null;
    private SweetAlertDialog _progressDialog;
    private Uri imageUri;
    private RecogniseFoodByImage _recogniseFoodImage = null;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private FirebaseFirestore database;
    private File imageFile;

    public TrackWithImageFragment() {
        user = getSessionHandler().getUser();
    }

    public static TrackWithImageFragment newInstance(String date, String mealType) {
        TrackWithImageFragment fragment = new TrackWithImageFragment();
        Bundle args = new Bundle();
        args.putString(TrackWithImageActivity.DATE_KEY, date);
        args.putString(TrackWithImageActivity.MEAL_TYPE_KEY, mealType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(TrackWithImageActivity.DATE_KEY)) {
                date = getArguments().getString(TrackWithImageActivity.DATE_KEY, "");
            }
            if (getArguments().containsKey(TrackWithImageActivity.MEAL_TYPE_KEY)) {
                mealType = getArguments().getString(TrackWithImageActivity.MEAL_TYPE_KEY, "");
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(_recogniseFoodImage!=null)
        {
            _recogniseFoodImage.cancel(true);
        }
        if(_uploadImage!=null)
        {
            _uploadImage.cancel(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_with_image, container, false);

        initialization(view);

        return view;
    }

    private void initialization(View view) {
        //bind view with id
        ImageView _cameraButton = view.findViewById(R.id.cameraButton);
        ImageView _libraryButton = view.findViewById(R.id.libraryButton);
        _imageView = view.findViewById(R.id.imageView);

        //set progress dialog
        _progressDialog = showProgressDialog("", getResources().getColor(R.color.green_A700));

        //initialize database
        database = FirebaseFirestore.getInstance();
        //setup storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/" + user.getUID());

        //when click camera
        _cameraButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //check the camera permission
                if (checkCameraPermission()) {
                    snapImageFromCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        //when click library
        _libraryButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //check the storage permission
                if (checkStoragePermission()) {
                    selectImageFromGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    //intent to crop image
                    Intent intent = new Intent(getContext(), CropImageActivity.class);
                    intent.putExtra(CropImageActivity.URI_STRING_KEY, Uri.fromFile(imageFile).toString());
                    startActivityForResult(intent, CROP_IMAGE_RESULT);
                } else {
                    removeImageFileSaved();
                }
                break;
            }
            case GALLERY_REQUEST_CODE: {
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    //create a new file and write the input stream from uri to new file
                    writeUriToNewFile(uri);
                    //intent to crop image
                    Intent intent = new Intent(getContext(), CropImageActivity.class);
                    intent.putExtra(CropImageActivity.URI_STRING_KEY, uri.toString());
                    startActivityForResult(intent, CROP_IMAGE_RESULT);

                } else {
                    removeImageFileSaved();
                }
                break;
            }
            case CROP_IMAGE_RESULT: {
                if (resultCode == RESULT_OK) {
                    _progressDialog.setContentText("Loading...");
                    _progressDialog.show();
                    //get the return uri
                    imageUri = Uri.parse(data.getStringExtra(CropImageActivity.RESULT_RETURN_KEY));

                    //set the image
                    Picasso.get().load(imageUri).fit().placeholder(R.drawable.ic_image_holder).into(_imageView, new Callback() {
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
                } else {
                    removeImageFileSaved();
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

    private void uploadImage(File imageFile) {
        if (_uploadImage == null) {
            _uploadImage = new UploadImageToStorage(imageFile);
            _uploadImage.execute();
        }
    }

    private void recogniseFoodImage(String imagePath) {
        if (_recogniseFoodImage == null) {
            _recogniseFoodImage = new RecogniseFoodByImage(imagePath);
            _recogniseFoodImage.execute();
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

    public void recognitionImage() {
        if (imageUri == null) {
            ErrorAlert("Please select or capture food image before start the detection", sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
        } else {
            uploadImage(imageFile);
        }
    }

    public void removeImageFileSaved() {
        if (imageFile != null && imageFile.exists())
            imageFile.delete();
    }

    private class UploadImageToStorage extends AsyncTask<Void, Void, Void> {
        private File imageFile;

        public UploadImageToStorage(File imageFile) {
            this.imageFile = imageFile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.setContentText("Loading...");
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Uri uri = compressImage(imageFile);
            StorageReference reference = storageReference.child(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(imageUri -> {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    recogniseFoodImage(imageUri.toString());
                });

            }).addOnFailureListener(e -> {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();
                ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
            });
            _uploadImage = null;
            return null;
        }
    }

    private Uri compressImage(File imageFile) {
        //compress the image
        File file = null;
        try {
            file = new Compressor(getContext()).setDestinationDirectoryPath(getContext().getFilesDir().getAbsolutePath()).
                    setCompressFormat(Bitmap.CompressFormat.JPEG).setQuality(50).compressToFile(imageFile);
            return Uri.fromFile(file);
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        return null;
    }

    private class RecogniseFoodByImage extends AsyncTask<Void, Void, Void> {
        private String imagePath;

        public RecogniseFoodByImage(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.setContentText("Recognising...");
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Call<FoodClassifyResponse> callFoodClassify = ApiClient.getIBMWatsonService().getFoodClassify(imagePath, "2018-03-19", "food");
            callFoodClassify.enqueue(new retrofit2.Callback<FoodClassifyResponse>() {
                @Override
                public void onResponse(Call<FoodClassifyResponse> call, Response<FoodClassifyResponse> response) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(() -> {
                            //if response not empty
                            if (response.body().foodImages[0].foodClassifiers[0].foodClasses != null) {
                                //get the result
                                FoodClass[] foodClasses = response.body().foodImages[0].foodClassifiers[0].foodClasses;
                                List<FoodClass> _foodClassList = new ArrayList<>();
                                //to check the image is food
                                boolean checkIsFood = true;
                                //loop the result
                                for (FoodClass foodClass : foodClasses) {
                                    //if appear the non-food result, direct break and set the boolean to false
                                    if (foodClass.Class.equals("non-food")) {
                                        checkIsFood = false;
                                        break;
                                    } else if (foodClass.Class.equals("food") || foodClass.Class.equals("meal") || foodClass.Class.equals("lunch")||
                                            foodClass.Class.equals("breakfast")||foodClass.Class.equals("dinner")) {
                                        //else if the result is "food","meal","lunch","breakfast","dinner" then continue, because it is meaningless to get it
                                        continue;
                                    }
                                    //then add the result into list
                                    _foodClassList.add(foodClass);
                                }
                                //if the boolean is food, then check the food is in database or not
                                if (checkIsFood) {
                                    checkFoodIsExistsInFoodDatabase(_foodClassList.get(0), imagePath);
                                } else {
                                    //if not food, remove the image file saved
                                    removeImageFileSaved();
                                    //remove the image already save in the storage
                                    StorageReference removeImageReference = storage.getReferenceFromUrl(imagePath);
                                    removeImageReference.delete();
                                    if (_progressDialog.isShowing())
                                        _progressDialog.dismiss();
                                    //proof out the error alert
                                    ErrorAlert("After recognise the image, the image is non food", sweetAlertDialog -> {
                                        sweetAlertDialog.dismiss();
                                        getActivity().finish();
                                    }, false).show();
                                }
                            } else {
                                ErrorAlert("There is some input error. Please try again later", sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                            }
                        });
                    } else {
                        ErrorAlert(response.message(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                    }
                }
                @Override
                public void onFailure(Call<FoodClassifyResponse> call, Throwable t) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    ErrorAlert(t.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                }
            });
            _recogniseFoodImage = null;
            return null;
        }
    }

    private void checkFoodIsExistsInFoodDatabase(FoodClass foodClass, String imagePath) {
        _progressDialog.setContentText("Checking Food database...");
        _progressDialog.show();

        //to check the food is exists in the food database or not
        MealDetailRequest _request = new MealDetailRequest(foodClass.Class);
        Call<MealDetailResponse> callCommonFoodDetailAPI = ApiClient.getNutritionixService().getCommonFoodDetail(_request);
        callCommonFoodDetailAPI.enqueue(new retrofit2.Callback<MealDetailResponse>() {
            @Override
            public void onResponse(Call<MealDetailResponse> call, Response<MealDetailResponse> response) {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();
                //if exists in food database
                if (response.isSuccessful()) {
                    if (response.body().foodDetail != null) {
                        Intent intent = new Intent(getContext(), MealDetailActivity.class);
                        intent.putExtra(MealDetailActivity.DATE_KEY, date);
                        intent.putExtra(MealDetailActivity.MEAL_TYPE_KEY, mealType);
                        intent.putExtra(MealDetailActivity.FOOD_NAME_KEY, foodClass.Class);
                        intent.putExtra(MealDetailActivity.FOOD_IMAGE_URL_KEY, imagePath);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        getActivity().finish();
                    }
                } else {
                    //if food no exists in food database
                    ErrorAlert(String.format("The food image you send is %1$s that currently not exists in the food database.", foodClass.Class), sweetAlertDialog -> {
                        //upload the no exists food to database
                        //create meal record class
                        Map<String, Object> foodSubmitMap = new HashMap<>();//create hash map to store the food submit data
                        foodSubmitMap.put("imageUrl", imagePath);
                        foodSubmitMap.put("foodName", foodClass.Class);
                        database.collection("NotFoundFoods").add(foodSubmitMap);
                        sweetAlertDialog.dismiss();
                        getActivity().finish();
                    }, false).show();
                }
            }
            @Override
            public void onFailure(Call<MealDetailResponse> call, Throwable t) {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();
                ErrorAlert(t.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
            }
        });
    }
}
