package com.bazzillion.ingrid.shelfie;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bazzillion.ingrid.shelfie.Adapters.BaseAdapter;
import com.bazzillion.ingrid.shelfie.Database.Repository;

public class BaseFragment extends Fragment {

    private static final String ARG_PRODUCT_TYPE = "product_type";
    private String productType;
    private RecyclerView recyclerView;
    private BaseAdapter baseAdapter;

    public BaseFragment() {
        // Required empty public constructor
    }


    public static BaseFragment newInstance(String productType) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_TYPE, productType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productType = getArguments().getString(ARG_PRODUCT_TYPE);
        }

        if (productType != null){
            baseAdapter = new BaseAdapter((BaseAdapter.BaseClickHandler) getActivity());
            Repository.getInstance(getContext()).retrieveBases(baseAdapter, productType, getContext());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        recyclerView = view.findViewById(R.id.base_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(baseAdapter);

        return view;
    }
}
