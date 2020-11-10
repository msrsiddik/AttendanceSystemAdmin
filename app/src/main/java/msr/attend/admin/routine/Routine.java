package msr.attend.admin.routine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import msr.attend.admin.R;

public class Routine extends Fragment {
    public Routine() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_routine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final LinearLayout linearLayout = view.findViewById(R.id.departCont);

        String[] depart = getResources().getStringArray(R.array.department_name);

        for (String d : depart){
            Button button = new Button(getContext());
            linearLayout.addView(button);
            button.setText(d);
            button.setOnClickListener(v -> {
                getFragmentManager().beginTransaction().replace(R.id.FragContainer, new RoutineView()).addToBackStack(null).commit();
            });
        }

    }
}