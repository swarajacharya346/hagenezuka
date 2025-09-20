package com.example.sih;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private TextView tvName, tvId;
    private Spinner languageSpinner;
    private Button logoutBtn;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvName = v.findViewById(R.id.workerName);
        tvId = v.findViewById(R.id.workerId);
        languageSpinner = v.findViewById(R.id.languageSpinner);
        logoutBtn = v.findViewById(R.id.logoutBtn);

        tvName.setText("Worker Name");
        tvId.setText("ID: 12345");
        languageSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"English","Hindi"}));

        logoutBtn.setOnClickListener(view -> FirebaseAuth.getInstance().signOut());
        return v;
    }
}
