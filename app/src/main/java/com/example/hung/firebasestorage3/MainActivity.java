package com.example.hung.firebasestorage3;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    FirebaseStorage storage;
    StorageReference storageRef ;
    StorageReference imagesRef;
    Uri file;
    UploadTask uploadTask;
    public static int REQUEST_CODE_PICK_IMAGE=1;
    String filePath;
    StorageReference islandRef;
    ImageView imv_show;
    Uri downloadUrl;
    TextView tv_link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imv_show=(ImageView)findViewById(R.id.imv_show);
        tv_link=(TextView)findViewById(R.id.tv_link);
        storage = FirebaseStorage.getInstance();
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public void onCreateReferenceClick(View view){
        storageRef  = storage.getReferenceFromUrl("gs://fbstorage-fd9ef.appspot.com");
        showToast("Reference Created Successfully.");
    }
    public void onCreateDirectoryClick(View view){
        imagesRef = storageRef.child("images");
        showToast("Reference directory Created Successfully.");
    }
    public void onUploadFileClick(View view){
       // pickImage();
         file = Uri.fromFile(new File("/storage/emulated/0/Download/images.jpeg"));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                 downloadUrl = taskSnapshot.getDownloadUrl();
                tv_link.setText(""+ downloadUrl);

            }
        });
    }

//    public void onDownloadFileClick(View view){
//        islandRef = storageRef.child("images/images.jpg");
//        final long ONE_MEGABYTE = 1024 * 1024;
//        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//                //imv_show.setImageBitmap();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
//    }


    public  void onDownloadFileClick(View view) throws IOException {
        islandRef = storageRef.child("images/images.jpeg");
        //islandRef = storageRef.child(file);

        final File localFile = File.createTempFile("images", "jpg");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                String picturepatch = localFile.getAbsolutePath();
                Bitmap bmp = BitmapFactory.decodeFile(picturepatch);

                //imv_view.setImageBitmap(getResizedBitmap(bmp,2000,2000));
                imv_show.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void onDeleteFileClick(View view){
        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child("images/images.jpeg");
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this,"File deleted successfully",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                //String filePath = FileUtil.getPath(this, data.getData());
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                 filePath = cursor.getString(columnIndex);
                 cursor.close();
               // file = Uri.fromFile(new File(filePath));
                //uploadFile(mUri);
            }
        }
    }

}
