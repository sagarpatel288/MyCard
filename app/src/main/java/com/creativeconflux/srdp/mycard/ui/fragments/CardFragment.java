package com.creativeconflux.srdp.mycard.ui.fragments;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.creativeconflux.srdp.mycard.R;
import com.creativeconflux.srdp.mycard.listeners.NestedScrollViewScrollStateListener;
import com.creativeconflux.srdp.mycard.pojos.Udacian;
import com.creativeconflux.srdp.mycard.ui.activities.MainActivity;
import com.creativeconflux.srdp.mycard.utils.AppConstants;
import com.creativeconflux.srdp.mycard.utils.IntentKeys;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.creativeconflux.srdp.mycard.utils.StringMethods.getNonNullString;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment implements NestedScrollView.OnScrollChangeListener, NestedScrollViewScrollStateListener {

    //region UI Variables
    @BindView(R.id.iv_profile_picture)
    ImageView ivProfilePicture;
    @BindView(R.id.tv_full_name)
    TextView tvFullName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.layout_grand_parent_linear)
    RelativeLayout layoutGrandParentRelative;
    @BindView(R.id.layout_root_nested_scroll)
    NestedScrollView layoutRootNestedScroll;
    //endregion
    Unbinder unbinder;

    public CardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {
            setImage(getArguments());
            if (getArguments().getParcelable(IntentKeys.PARCEL_UDACIAN) != null) {
                setObserver((Udacian) getArguments().getParcelable(IntentKeys.PARCEL_UDACIAN));
            }
        }
        hideSystemUI(layoutRootNestedScroll);
        setListeners();
        return view;
    }

    private void setListeners() {
        tvAddress.setMovementMethod(new ScrollingMovementMethod());
        tvAddress.setSelected(true);
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        layoutRootNestedScroll.setScrollListener(this);
//        layoutRootNestedScroll.setOnScrollChangeListener(this);
    }

    private void setImage(Bundle arguments) {
        if (arguments.getString(IntentKeys.STRING_IMAGE_PATH) != null) {
            Glide.with(this)
                    .load(arguments.getString(IntentKeys.STRING_IMAGE_PATH))
                    .thumbnail(0.50f)
                    .apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().dontTransform())
                    .into(ivProfilePicture);
        }
    }

    private void setObserver(Udacian udacian) {
        /*Reserved for MVVM*/
//        if (udacian != null) {
//            UdacianViewModel udacianViewModel = ViewModelProviders.of(this).get(UdacianViewModel.class);
//            udacianViewModel.init(udacian.getEmailId());
//            udacianViewModel.getUdacian().observe(this, new Observer<Udacian>() {
//                @Override
//                public void onChanged(@Nullable Udacian udacian) {
//                    setData(udacian);
//                }
//            });
//        }
        setData(udacian);
    }

    private void setData(Udacian udacian) {
        tvFullName.setText(udacian.getFullName());
        tvAddress.setText(udacian.getAddress());
        tvEmail.setText(udacian.getEmailId());
        tvContact.setText(getNonNullString(udacian.getContactNumber(), ""));
    }

    // This snippet hides the system bars.
    private void hideSystemUI(View mDecorView) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (getUserVisibleHint()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mDecorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
            if (((MainActivity) getActivity()) != null) {
                ((MainActivity) getActivity()).hideToolbar();
            }
        }
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI(View mDecorView) {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (((MainActivity) getActivity()) != null) {
            ((MainActivity) getActivity()).showToolbar();
        }
    }

    private void hideSystemUiWithDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSystemUI(layoutRootNestedScroll);
            }
        }, AppConstants.TIME_OUT_IMMERSIVE_MODE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//        hideSystemUI(layoutRootNestedScroll);
    }

    @Override
    public void onNestedScrollViewStateChanged(int state) {
        /*if (state == RecyclerView.SCROLL_STATE_IDLE){
            hideSystemUI(layoutRootNestedScroll);
        } else {
            showSystemUI(layoutRootNestedScroll);
        }*/
    }
}
