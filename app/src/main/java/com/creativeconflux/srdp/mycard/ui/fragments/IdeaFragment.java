package com.creativeconflux.srdp.mycard.ui.fragments;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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
import com.creativeconflux.srdp.mycard.ui.customviews.MyNestedScrollView;
import com.creativeconflux.srdp.mycard.utils.AppConstants;
import com.creativeconflux.srdp.mycard.utils.IntentKeys;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.creativeconflux.srdp.mycard.utils.StringMethods.getNonNullString;

/**
 * A simple {@link Fragment} subclass.
 */
public class IdeaFragment extends Fragment implements NestedScrollView.OnScrollChangeListener, NestedScrollViewScrollStateListener {

    @BindView(R.id.iv_profile_picture)
    ImageView ivProfilePicture;
    @BindView(R.id.tv_full_name_title)
    TextView tvFullNameTitle;
    @BindView(R.id.tv_full_name)
    TextView tvFullName;
    @BindView(R.id.tv_username_title)
    TextView tvUsernameTitle;
    @BindView(R.id.tv_slack_username)
    TextView tvSlackUsername;
    @BindView(R.id.tv_email_id_title)
    TextView tvEmailIdTitle;
    @BindView(R.id.tv_email_id)
    TextView tvEmailId;
    @BindView(R.id.tv_contact_title)
    TextView tvContactTitle;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.tv_scholarship_track_title)
    TextView tvScholarshipTrackTitle;
    @BindView(R.id.tv_scholarship)
    TextView tvScholarship;
    @BindView(R.id.tv_github_profile_title)
    TextView tvGithubProfileTitle;
    @BindView(R.id.tv_github_profile)
    TextView tvGithubProfile;
    @BindView(R.id.tv_idea_title)
    TextView tvIdeaTitle;
    @BindView(R.id.tv_idea)
    TextView tvIdea;
    @BindView(R.id.tv_resource_link_title)
    TextView tvResourceLinkTitle;
    @BindView(R.id.tv_resource_link)
    TextView tvResourceLink;
    @BindView(R.id.tv_experience_title)
    TextView tvExperienceTitle;
    @BindView(R.id.tv_experience)
    TextView tvExperience;
    @BindView(R.id.tv_problem_solution_title)
    TextView tvProblemSolutionTitle;
    @BindView(R.id.tv_problem_solution)
    TextView tvProblemSolution;
    @BindView(R.id.tv_about_journey_title)
    TextView tvAboutJourneyTitle;
    @BindView(R.id.tv_about_journey)
    TextView tvAboutJourney;
    @BindView(R.id.layout_grand_parent_relative)
    RelativeLayout layoutGrandParentRelative;
    @BindView(R.id.layout_root_nested_scroll)
    MyNestedScrollView layoutRootNestedScroll;
    Unbinder unbinder;

    public IdeaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_idea, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {
            setImage(getArguments());
            if (getArguments().getParcelable(IntentKeys.PARCEL_UDACIAN) != null){
                setObserver((Udacian) getArguments().getParcelable(IntentKeys.PARCEL_UDACIAN));
            }
        }
        hideSystemUI(layoutRootNestedScroll);
        setListeners();
        return view;
    }

    private void setListeners() {
        layoutRootNestedScroll.setScrollListener(this);
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

    private void setObserver(Udacian udacian){
//        if (udacian != null){
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
        tvSlackUsername.setText(udacian.getUserName());
        tvEmailId.setText(udacian.getEmailId());
        tvContact.setText(getNonNullString(udacian.getContactNumber(), ""));
        tvScholarship.setText(udacian.getScholarshipTrack());
        tvGithubProfile.setText(udacian.getGithubProfile());
        tvIdea.setText(udacian.getIdea());
        tvResourceLink.setText(udacian.getIdeaResourceLink());
        tvExperience.setText(getNonNullString(udacian.getExperience(), ""));
        tvProblemSolution.setText(getNonNullString(udacian.getProbAndSols(), ""));
        tvAboutJourney.setText(getNonNullString(udacian.getAbout(), ""));
    }


    // This snippet hides the system bars.
    private void hideSystemUI(View mDecorView) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (getUserVisibleHint()){
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

    private void hideSystemUiWithDelay(){
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
