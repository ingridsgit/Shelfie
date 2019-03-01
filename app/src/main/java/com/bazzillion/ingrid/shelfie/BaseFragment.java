package com.bazzillion.ingrid.shelfie;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseFragment extends Fragment {

    private static final String ARG_PRODUCT_TYPE = "product_type";
    private static final String FIREBASE_KEY_PRODUCT = "product";
    private String productType;
    private RecyclerView recyclerView;
    private DatabaseReference dbReference;
    private BaseAdapter baseAdapter;
    private ValueEventListener valueEventListener;

    private OnFragmentInteractionListener mListener;

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
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            dbReference = database.getReference().child(FIREBASE_KEY_PRODUCT).child(productType);
            baseAdapter = new BaseAdapter((BaseAdapter.BaseClickHandler) getActivity());
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> valueSet = (ArrayList<String>) dataSnapshot.getValue();
                    baseAdapter.setBases(valueSet);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }

            };
            dbReference.addValueEventListener(valueEventListener);
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

    @Override
    public void onDetach() {
        super.onDetach();
        dbReference.removeEventListener(valueEventListener);
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
