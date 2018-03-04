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
import android.support.design.widget.TextInputLayout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.creativeconflux.srdp.mycard.R;
import com.creativeconflux.srdp.mycard.listeners.OnPermissionListener;
import com.creativeconflux.srdp.mycard.listeners.OnSelectImageListener;
import com.creativeconflux.srdp.mycard.pojos.MyColorObj;
import com.creativeconflux.srdp.mycard.pojos.Udacian;
import com.creativeconflux.srdp.mycard.ui.activities.MainActivity;
import com.creativeconflux.srdp.mycard.ui.dialogs.MyDialogFrag;
import com.creativeconflux.srdp.mycard.utils.AppConstants;
import com.creativeconflux.srdp.mycard.utils.FileUtils;
import com.creativeconflux.srdp.mycard.utils.IntentKeys;
import com.creativeconflux.srdp.mycard.utils.KeyboardUtils;
import com.creativeconflux.srdp.mycard.utils.LimitUtils;
import com.creativeconflux.srdp.mycard.utils.PermissionUtils;
import com.creativeconflux.srdp.mycard.utils.PrefKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.creativeconflux.srdp.mycard.utils.AppConstants.REQUEST_PERMISSION_SETTINGS;
import static com.creativeconflux.srdp.mycard.utils.SharedPref.getEditor;
import static com.creativeconflux.srdp.mycard.utils.SharedPref.getSharedPref;
import static com.creativeconflux.srdp.mycard.utils.StringMethods.getNonNullString;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCardFragment extends Fragment implements OnSelectImageListener, View.OnClickListener,
        NestedScrollView.OnScrollChangeListener, OnPermissionListener {

    //region Compile time UI Variables
    @BindView(R.id.tv_select_image)
    TextView tvSelectImage;
    @BindView(R.id.iv_profile_picture)
    ImageView ivProfilePicture;
    @BindView(R.id.ibtn_select_profile_picture)
    ImageButton ibtnSelectProfilePicture;
    @BindView(R.id.tiet_full_name)
    TextInputEditText tietFullName;
    @BindView(R.id.tiet_address)
    TextInputEditText tietAddress;
    @BindView(R.id.tiet_email_id)
    TextInputEditText tietEmailId;
    @BindView(R.id.tiet_contact)
    TextInputEditText tietContact;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.layout_grand_parent_relative)
    RelativeLayout layoutGrandParentRelative;
    @BindView(R.id.layout_root_nested_scroll)
    NestedScrollView layoutRootNestedScroll;
    Unbinder unbinder;
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
    private PermissionUtils permissionUtils;
    //endregion

    //region Runtime UI Variables
    private MyDialogFrag myDialogFrag;
    //endregion

    public EditCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            if (permissionUtils.isAllPermissionsGranted(getContext(), permissions, getString(R.string.des_permission))) {
                //Got Permission
                showDialogForImage();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_add_edit_card, container, false);
        mContext = view.getContext();
        hashMap = new HashMap<>();
        permissionUtils = new PermissionUtils(null, this, this);
        unbinder = ButterKnife.bind(this, view);
        setListeners();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

            //Prepare the Udacian object for parcel
            Bundle bundle = new Bundle();
            Udacian udacian = new Udacian(
                    tietFullName.getText().toString(),
                    tietEmailId.getText().toString(),
                    tietContact.getText().toString(),
                    tietAddress.getText().toString()
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
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_empty_img), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietFullName.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietAddress.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_msg_empty_address), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietEmailId.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(tietEmailId.getText().toString()).matches()) {
            Toast.makeText(mContext, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietContact.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.error_msg_empty_contact), Toast.LENGTH_SHORT).show();
            return false;
        } else if (tietContact.getText().toString().length() < LimitUtils.LIMIT_MIN_PHONE_NUMBER) {
            Toast.makeText(mContext, getString(R.string.error_invalid_contact), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDialogForImage() {
        myDialogFrag = new MyDialogFrag();
        myDialogFrag.setOnSelectImageListener(this);
        myDialogFrag.show(getActivity().getFragmentManager(), getString(R.string.dialog_img));
    }

    private void setListeners() {
        ibtnSelectProfilePicture.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onSelectImage(String imagePath) {
        this.imagePath = imagePath;
        // Show the thumbnail on ImageView
        Uri imageUri = Uri.parse(imagePath);
//        ivProfilePicture.setImageBitmap(FileUtils.getBitmapFromUri(imageUri));

        // Using glide for better image handling
        myDialogFrag.dismiss();

        Glide.with(EditCardFragment.this)
                .load(imagePath)
                .thumbnail(0.50f)
                .apply(new RequestOptions().centerCrop())
                .into(ivProfilePicture);

        Bitmap bitmap = FileUtils.getBitmapFromUri(imageUri);
        Palette palette = createPaletteSync(bitmap);
        setPalette(palette);
        setSwatchList(palette.getSwatches());
        setToolbar();
        setColorMap();
    }

    private Palette createPaletteSync(Bitmap bitmap) {
        setPalette(Palette.from(bitmap).generate());
        return getPalette();
    }

    private void setToolbar() {
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

    private void setColorMap() {
        if (getVibrantSwatch(getPalette()) != null) {
            hashMap.put(IntentKeys.INT_COLOR_TITLE, getVibrantSwatch(getPalette()).getTitleTextColor());
            hashMap.put(IntentKeys.INT_COLOR_HEADER, getVibrantSwatch(getPalette()).getTitleTextColor());
        } else {
            if (getSwatchList().size() > 0) {
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
                // TODO: 3/4/2018 by sagar  Consider using utils
                if (permissionUtils.isAllPermissionsGranted(v.getContext(), permissions, getString(R.string.des_permission))) {
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

    @Override
    public void OnPermissionGranted() {

    }

    @Override
    public void OnPermissionDenied() {

    }
}
