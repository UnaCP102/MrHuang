package unalee.mrhuang;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.security.PrivateKey;
import java.util.List;

public class ProfilePageFragment extends Fragment {
    private static final int RESULT_OK = -1;
    private ImageView imageView;
    private ImageButton ibChange;
    private File file;
    private static final int REQUEST_TAKE_PICTURE_SMALL = 0;
    private static final int REQUEST_PICK_PICTURE = 1;
    private ProfilePageFragment context;

    public ProfilePageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView = getActivity().findViewById(R.id.ivProfilePicture);
        ibChange = getActivity().findViewById(R.id.ibChange);
        context = ProfilePageFragment.this;

        ibChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_PICTURE);
                ibChange.bringToFront();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            int newSize = 512;
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE_SMALL:  //縮圖
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Bitmap picture = (Bitmap) bundle.get("data");
                        imageView.setImageBitmap(picture);
                    }
                    break;
                case REQUEST_PICK_PICTURE:    //挑圖
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String[] columns = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getActivity().getContentResolver().query(uri, columns,
                                null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String imagePath = cursor.getString(0);
                            cursor.close();
                            Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                            Bitmap downsizedImage = Common.downSize(srcImage, newSize);
                            imageView.setImageBitmap(downsizedImage);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Common.askPermissions(getActivity(), permissions, Common.REQ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ibChange.setEnabled(true);
                } else {
                    ibChange.setEnabled(false);
                }
                break;
        }
    }


}




