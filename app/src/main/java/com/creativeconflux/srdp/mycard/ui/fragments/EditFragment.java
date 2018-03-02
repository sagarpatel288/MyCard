package com.creativeconflux.srdp.mycard.ui.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.creativeconflux.srdp.mycard.R;
import com.creativeconflux.srdp.mycard.listeners.OnSelectImageListener;
import com.creativeconflux.srdp.mycard.pojos.MyColorObj;
import com.creativeconflux.srdp.mycard.pojos.Udacian;
import com.creativeconflux.srdp.mycard.ui.activities.MainActivity;
import com.creativeconflux.srdp.mycard.ui.dialogs.MyDialogFrag;
import com.creativeconflux.srdp.mycard.utils.AppConstants;
import com.creativeconflux.srdp.mycard.utils.FileUtils;
import com.creativeconflux.srdp.mycard.utils.IntentKeys;
import com.creativeconflux.srdp.mycard.utils.KeyboardUtils;
import com.creativeconflux.srdp.mycard.utils.PrefKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.creativeconflux.srdp.mycard.utils.AppConstants.REQUEST_PERMISSION_SETTINGS;
import static com.creativeconflux.srdp.mycard.utils.SharedPref.getEditor;
import static com.creativeconflux.srdp.mycard.utils.SharedPref.getSharedPref;
import static com.creativeconflux.srdp.mycard.utils.StringMethods.getNonNullString;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment implements OnSelectImageListener, View.OnClickListener,
            NestedScrollView.OnScrollChangeListener {

    //region Compile time UI Variables
    private TextInputEditText tietFullName;
    private TextInputEditText tietUsername;
    private TextInputEditText tietEmailId;
    private TextInputEditText tietContact;
    private RadioButton rbtnAndroidBasic;
    private RadioButton rbtnAndroidIntermediate;
    private RadioButton rbtnWebBasic;
    private RadioButton rbtnMobiWebIntermediate;
    private TextInputEditText tietGitHubProfile;
    private TextInputEditText tietIdea;
    private TextInputEditText tietResourceLink;
    private TextInputEditText tietExperience;
    private TextInputEditText tietProbSols;
    private TextInputEditText tietAbout;
    private ImageView ivProfilePicture;
    private Button btnSave;
    private ImageButton ibtnSelectProfile;
    private NestedScrollView nestedScrollView;
    //endregion

    //region Class Variables
    private Context mContext;
    private String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private boolean sentToSettings;
    private String imagePath;
    private Palette palette;
    private List<Palette.Swatch> swatchList;
    private MyColorObj myColorObj;
    private HashMap<String, Integer> hashMap;
    //endregion

    //region Runtime UI Variables
    private MyDialogFrag myDialogFrag;
    //endregion

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            if (allPermissionsGranted()) {
                //Got Permission
                showDialogForImage();
            }
        }
    }
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        mContext = view.getContext();
        setViews(view);
        hashMap = new HashMap<>();
        return view;
    }

    private void setViews(View view) {
        nestedScrollView = view.findViewById(R.id.layout_root_nested_scroll);
        btnSave = view.findViewById(R.id.btn_save);
        tietFullName = view.findViewById(R.id.tiet_full_name);
        tietUsername = view.findViewById(R.id.tiet_slack_username);
        tietEmailId = view.findViewById(R.id.tiet_email_id);
        tietContact = view.findViewById(R.id.tiet_contact);
        rbtnAndroidBasic = view.findViewById(R.id.rbtn_android_basics);
        rbtnAndroidIntermediate = view.findViewById(R.id.rbtn_android_developer);
        rbtnWebBasic = view.findViewById(R.id.rbtn_front_end_web_basic);
        rbtnMobiWebIntermediate = view.findViewById(R.id.rbtn_mobile_web);
        tietGitHubProfile = view.findViewById(R.id.tiet_github_profile);
        tietIdea = view.findViewById(R.id.tiet_idea);
        tietResourceLink = view.findViewById(R.id.tiet_resource_link);
        tietExperience = view.findViewById(R.id.tiet_experience);
        tietProbSols = view.findViewById(R.id.tiet_problem);
        tietAbout = view.findViewById(R.id.tiet_about_journey);
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        ibtnSelectProfile = view.findViewById(R.id.ibtn_select_profile_picture);
        setListeners();
    }

    private void setListeners() {
        btnSave.setOnClickListener(this);
        ibtnSelectProfile.setOnClickListener(this);
        nestedScrollView.setOnScrollChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.menu_item_save).setVisible(true);
        menu.findItem(R.id.menu_item_edit).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                onClickSave();
                break;
        }
        return true;
    }

    private void onClickSave() {
        //Checks if all mandatory fields have been filled or not
        if (isValid()) {
            //Checks which radio button has been checked to determine the value of scholarship track
            String scholarshipTrack = null;
            if (rbtnAndroidBasic.isChecked()) {
                scholarshipTrack = getString(R.string.label_android_basics);
            } else if (rbtnAndroidIntermediate.isChecked()) {
                scholarshipTrack = getString(R.string.label_android_developer);
            } else if (rbtnWebBasic.isChecked()) {
                scholarshipTrack = getString(R.string.label_front_end_web_basic);
            } else if (rbtnMobiWebIntermediate.isChecked()) {
                scholarshipTrack = getString(R.string.label_mobile_web);
            }

            //Prepare the Udacian object for parcel
            Bundle bundle = new Bundle();
            Udacian udacian = new Udacian(
                    tietFullName.getText().toString(),
                    tietUsername.getText().toString(),
                    tietEmailId.getText().toString(),
                    getNonNullString(tietContact, ""),
                    scholarshipTrack,
                    tietGitHubProfile.getText().toString(),
                    tietIdea.getText().toString(),
                    tietResourceLink.getText().toString(),
                    getNonNullString(tietExperience, ""),
                    getNonNullString(tietProbSols, ""),
                    getNonNullString(tietAbout, "")
            );

            bundle.putParcelable(IntentKeys.PARCEL_UDACIAN, udacian);

            //If profile image has been selected, we can get color reference to use later.
            if (hashMap != null) {
                myColorObj = new MyColorObj(hashMap);
                bundle.putParcelable(IntentKeys.PARCEL_COLOR_OBJ, myColorObj);
            }

            //If profile image has been selected, we will use it for final card
            if (imagePath != null) {
                bundle.putString(IntentKeys.STRING_IMAGE_PATH, imagePath);
            }
            ((MainActivity) getActivity()).commitFragmentTransaction(new CardFragment(), true, bundle);
        }
    }

    private boolean isValid() {
        if (tietFullName.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietUsername.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_empty_username), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietEmailId.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(tietEmailId.getText().toString()).matches()) {
            Toast.makeText(mContext, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!rbtnAndroidBasic.isChecked() && !rbtnAndroidIntermediate.isChecked()
                && !rbtnWebBasic.isChecked() && !rbtnMobiWebIntermediate.isChecked()) {
            Toast.makeText(mContext, getString(R.string.error_no_scholarship_track), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietGitHubProfile.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_empty_git_profile), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.WEB_URL.matcher(tietGitHubProfile.getText().toString()).matches()) {
            Toast.makeText(mContext, getString(R.string.error_invalid_git_profile), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietIdea.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_msg_empty_idea), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietResourceLink.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_empty_idea_resource_link), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.WEB_URL.matcher(tietResourceLink.getText().toString()).matches()){
            Toast.makeText(mContext, getString(R.string.error_invalid_idea_resource_link), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean allPermissionsGranted() {
        //Check if permission has granted or not
        if (!checkPermissions(permissions)) {
            //Check if permission has denied with or without "don't ask again" option
            //If "don't ask again" was not checked, show reason for permission
            if (shouldShowRational(permissions)) {
                showPermissionReason();
                //If "don't ask again" was checked, show dialog to redirect to permission settings
            } else if (getSharedPref(mContext).getBoolean(PrefKeys.ALREADY_ASKED_PERMISSION, false)) {
                redirectToSettings();
                //Otherwise, it is very first time the permission dialog is going to be opened
            } else {
                askForPermission();
            }
            //Store that we have already asked for permission despite of what user has granted and what not
            getEditor(mContext).putBoolean(PrefKeys.ALREADY_ASKED_PERMISSION, true);
            getEditor(mContext).apply();
            return false;

        } else {
            return true;
        }
    }

    private void showDialogForImage() {
        myDialogFrag = new MyDialogFrag();
        myDialogFrag.setOnSelectImageListener(this);
        myDialogFrag.show(getActivity().getFragmentManager(), getString(R.string.dialog_img));
    }

    private boolean checkPermissions(String[] permissions) {
        for (int i = 0; i < permissions.length - 1; i++) {
            if (ActivityCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldShowRational(String[] permissions) {

        List<String> nonGranted = new ArrayList<>();

        for (int i = 0; i < permissions.length - 1; i++) {
            if (ActivityCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                nonGranted.add(permissions[i]);
            }
        }

        String[] permissionsToCheck = (String[]) nonGranted.toArray(new String[0]);

        for (int i = 0; i < permissionsToCheck.length - 1; i++) {
            if (!shouldShowRequestPermissionRationale(permissionsToCheck[i])) {
                return false;
            }
        }
        return true;
    }

    private void showPermissionReason() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.title_dialog_permission));
        builder.setMessage(getString(R.string.des_permission));
        builder.setPositiveButton(getString(R.string.label_grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                askForPermission();
            }
        });
        builder.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void redirectToSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.title_dialog_permission));
        builder.setMessage(getString(R.string.des_permission));
        builder.setPositiveButton(getString(R.string.label_grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sentToSettings = true;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(getString(R.string.scheme_package), mContext.getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS);
            }
        });
        builder.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void askForPermission() {
        List<String> nonGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length - 1; i++) {
            if (ActivityCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                nonGranted.add(permissions[i]);
            }
        }
        String[] permissions = (String[]) nonGranted.toArray(new String[0]);
        requestPermissions(permissions, AppConstants.REQUEST_PERMISSION_CALLBACK);
    }

    @Override
    public void onSelectImage(String imagePath) {
        this.imagePath = imagePath;
        // Show the thumbnail on ImageView
        Uri imageUri = Uri.parse(imagePath);
//        ivProfilePicture.setImageBitmap(FileUtils.getBitmapFromUri(imageUri));

        // Using glide for better image handling
        myDialogFrag.dismiss();

        Glide.with(EditFragment.this)
                .load(imagePath)
                .thumbnail(0.50f)
                .apply(new RequestOptions().centerCrop())
                .into(ivProfilePicture);

        Bitmap bitmap = FileUtils.getBitmapFromUri(imageUri);
        Palette palette = createPaletteSync(bitmap);
        setPalette(palette);
        setSwatchList(palette.getSwatches());
        setToolbar(bitmap);
//        setTextColors(bitmap);
    }

    private Palette createPaletteSync(Bitmap bitmap) {
        setPalette(Palette.from(bitmap).generate());
        return getPalette();
    }

    private void setToolbar(Bitmap bitmap) {
        if (((MainActivity) getActivity()) != null && ((MainActivity) getActivity()).getToolbar() != null) {
            Palette.Swatch swatch = getVibrantSwatch(getPalette());
            if (swatch != null) {
                applySwatchToolbar(swatch);
            } else {
                List<Palette.Swatch> swatchList = getPalette().getSwatches();
                if (swatchList.size() > 0) {
                    applySwatchToolbar(swatchList.get(0));
                }
            }
        }
    }

    public Palette getPalette() {
        return palette;
    }

    private Palette.Swatch getVibrantSwatch(Palette palette) {
        return palette.getVibrantSwatch();
    }

    private void applySwatchToolbar(Palette.Swatch swatch) {
        ((MainActivity) getActivity()).getToolbar().setBackgroundColor(swatch.getRgb());
        ((MainActivity) getActivity()).getToolbar().setTitleTextColor(swatch.getTitleTextColor());
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    private void setTextColors(Bitmap bitmap) {
        if (getVibrantSwatch(getPalette()) != null) {
            tietFullName.setTextColor(getVibrantSwatch(getPalette()).getTitleTextColor());
            tietEmailId.setTextColor(getVibrantSwatch(getPalette()).getTitleTextColor());
            hashMap.put(IntentKeys.INT_COLOR_TITLE, getVibrantSwatch(getPalette()).getTitleTextColor());
            hashMap.put(IntentKeys.INT_COLOR_HEADER, getVibrantSwatch(getPalette()).getTitleTextColor());
        } else {
            if (getSwatchList().size() > 0) {
                tietFullName.setTextColor(getSwatchList().get(0).getTitleTextColor());
                tietEmailId.setTextColor(getSwatchList().get(0).getTitleTextColor());
                hashMap.put(IntentKeys.INT_COLOR_TITLE, getSwatchList().get(0).getTitleTextColor());
                hashMap.put(IntentKeys.INT_COLOR_HEADER, getSwatchList().get(0).getTitleTextColor());
            }
        }

        if (getDominantSwatch(getPalette()) != null) {

            hashMap.put(IntentKeys.INT_COLOR_FOOTER, getDominantSwatch(getPalette()).getBodyTextColor());
        } else {
            if (getSwatchList().size() > 0) {

                hashMap.put(IntentKeys.INT_COLOR_FOOTER, getSwatchList().get(0).getBodyTextColor());
            }
        }
    }

    public List<Palette.Swatch> getSwatchList() {
        return swatchList;
    }

    public void setSwatchList(List<Palette.Swatch> swatchList) {
        this.swatchList = swatchList;
    }

    private Palette.Swatch getDominantSwatch(Palette palette) {
        return palette.getDominantSwatch();
    }

    private Palette.Swatch getLightMutedSwatch(Palette palette) {
        return palette.getLightMutedSwatch();
    }

    private Palette.Swatch getLightVibrantSwatch(Palette palette) {
        return palette.getLightVibrantSwatch();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_select_profile_picture:
                if (allPermissionsGranted()) {
                    showDialogForImage();
                }
                break;

            case R.id.btn_save:
                onClickSave();
                break;
        }
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        KeyboardUtils.hideKeyboard(getActivity());
    }
}
