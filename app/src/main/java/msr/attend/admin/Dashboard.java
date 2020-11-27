package msr.attend.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class Dashboard extends Fragment {
    private ImageButton teacherBtn, studentBtn;
    private FragmentInterface fragmentInterface;

    public Dashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        teacherBtn = view.findViewById(R.id.teacherBtn);
        studentBtn = view.findViewById(R.id.studentBtn);

        fragmentInterface = (FragmentInterface) getActivity();

        teacherBtn.setOnClickListener(v -> gotoTeacher());
        studentBtn.setOnClickListener(v -> gotoStudent());
    }

    private void gotoStudent() {
        fragmentInterface.student();
    }

    void gotoTeacher(){
        fragmentInterface.teacher();
    }
}