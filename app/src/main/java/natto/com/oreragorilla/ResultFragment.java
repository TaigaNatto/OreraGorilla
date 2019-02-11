package natto.com.oreragorilla;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import natto.com.oreragorilla.databinding.FragmentCameraBinding;

public class ResultFragment extends Fragment {

    public ResultFragment() {
        FragmentCameraBinding binding;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }
}
