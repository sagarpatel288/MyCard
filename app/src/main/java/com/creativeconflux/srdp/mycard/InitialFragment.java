package com.creativeconflux.srdp.mycard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.creativeconflux.srdp.mycard.ui.activities.MainActivity;
import com.creativeconflux.srdp.mycard.ui.fragments.EditCardFragment;
import com.creativeconflux.srdp.mycard.ui.fragments.EditIdeaFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class InitialFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.btn_make_my_card)
    Button btnMakeMyCard;
    @BindView(R.id.btn_submit_idea)
    Button btnSubmitIdea;
    Unbinder unbinder;

    public InitialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_initial, container, false);
        unbinder = ButterKnife.bind(this, view);
        setListeners();
        return view;
    }

    private void setListeners() {
        btnMakeMyCard.setOnClickListener(this);
        btnSubmitIdea.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_make_my_card:
                ((MainActivity) getActivity()).commitFragmentTransaction(new EditCardFragment(), true, null);
                break;

            case R.id.btn_submit_idea:
                ((MainActivity) getActivity()).commitFragmentTransaction(new EditIdeaFragment(), true, null);
                break;
        }
    }
}
