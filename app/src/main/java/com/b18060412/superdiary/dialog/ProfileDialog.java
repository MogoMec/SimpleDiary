package com.b18060412.superdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.b18060412.superdiary.FileManager;
import com.b18060412.superdiary.MainActivity;
import com.b18060412.superdiary.ProfileManager;
import com.b18060412.superdiary.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.nio.file.ClosedFileSystemException;

import static android.app.Activity.RESULT_OK;


public class ProfileDialog extends DialogFragment implements View.OnClickListener {


    private static final String TAG = "ProfileDialog";
    private ImageView IV_profile_image;
    private EditText ET_profile_name;
    private Button BTN_ok,BTN_cancel;

    private Boolean isAddNewProfilePicture = false;
    private FileManager tempFileManager;
    private String profilePictureFileName = "";

    public interface YourNameCallback {
        void updateName();
    }
    private YourNameCallback callback;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.IV_profile_image:
                Intent intentImage = new Intent();
                intentImage.setType("image/*");
                intentImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentImage, "Select Picture"), 1);
            case R.id.BT_cancel:
                dismiss();
                break;
            case R.id.BT_ok:
                saveProfile();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.initProfile();
                dismiss();
                break;


        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: " +requestCode);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    tempFileManager = new FileManager(getActivity(), FileManager.TEMP_DIR);
                    tempFileManager.clearDir();

                    int photoSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,getResources().getDisplayMetrics());
                    UCrop.Options options = new UCrop.Options();

                    UCrop.of(data.getData(), Uri.fromFile(
                            new File(tempFileManager.getDir() + "/" + FileManager.createRandomFileName())))
                            .withMaxResultSize(photoSize, photoSize)
                            .withAspectRatio(1, 1)
                            .withOptions(options)
                            .start(getActivity(), this);
                } else {
                    Toast.makeText(getActivity(),"获取图片资源失败", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri resultUri = UCrop.getOutput(data);
                    IV_profile_image.setImageBitmap(BitmapFactory.decodeFile(resultUri.getPath()));
                    profilePictureFileName = FileManager.getFileNameByUri(getActivity(), resultUri);
                    isAddNewProfilePicture = true;
                } else {
                    Toast.makeText(getActivity(), "裁剪失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void saveProfile() {
        ProfileManager.setName(getActivity(),ET_profile_name.getText().toString());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (YourNameCallback) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_profile,container);
        IV_profile_image = rootView.findViewById(R.id.IV_profile_image);
        ET_profile_name = rootView.findViewById(R.id.ET_profile_name);
        ET_profile_name.setText(ProfileManager.getName(getActivity()));
        BTN_cancel = rootView.findViewById(R.id.BT_cancel);
        BTN_ok = rootView.findViewById(R.id.BT_ok);
        BTN_cancel.setOnClickListener(this);
        BTN_ok.setOnClickListener(this);
        IV_profile_image.setOnClickListener(this);
        return rootView;
    }





    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
