package ru.mirea.khrechkorv.mireaproject.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.khrechkorv.mireaproject.R;

public class CameraFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 100;
    private Uri imageUri;
    private ImageView image;
    private EditText editText;
    private	boolean	isWork = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data	= result.getData();
                    image.setImageURI(imageUri);
                    ClearText();
                }
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_camera, container, false);
        super.onCreate(savedInstanceState);
        image = root.findViewById(R.id.imageView);
        editText = root.findViewById(R.id.editTextText2);
        image.setOnClickListener(view ->  openCamera());

        checkPermission();
        return  root;
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
            isWork = true;
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            isWork = false;
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (isWork) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(requireContext(),
                        "ru.mirea.khrechkorv.mireaproject.fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                takePictureLauncher.launch(cameraIntent);
            }
        }
    }

    private	File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMAGE_"	+ timeStamp + "_";
        File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
    }

    private void ClearText(){
        editText.setText("");
    }
}
