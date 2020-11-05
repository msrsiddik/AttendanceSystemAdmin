package msr.attend.admin.Teacher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import msr.attend.admin.FirebaseDatabaseHelper;
import msr.attend.admin.Model.CoordinatorModel;
import msr.attend.admin.Model.TeacherModel;
import msr.attend.admin.R;
import msr.attend.admin.gson.Utils;

public class TeacherProfile extends Fragment {
    private TextView tName, tEmail, tPhone, tDepart;
    private LinearLayout tCoCoordLayout;
    private Spinner tBatchSpin;
    private Button coordinatorAddBtn;
    private TeacherModel teacherModel;

    public TeacherProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tName = view.findViewById(R.id.tName);
        tEmail = view.findViewById(R.id.tEmail);
        tPhone = view.findViewById(R.id.tPhone);
        tDepart = view.findViewById(R.id.tDepart);
        tCoCoordLayout = view.findViewById(R.id.tCoCoordLayout);
        tBatchSpin = view.findViewById(R.id.tBatch);
        coordinatorAddBtn = view.findViewById(R.id.coordinatorAddBtn);

        Bundle bundle = getArguments();
        teacherModel = Utils.getGsonParser().fromJson(bundle.getString("teacher"),TeacherModel.class);
        tName.setText(teacherModel.getName());
        tEmail.setText(teacherModel.getEmail());
        tPhone.setText(teacherModel.getPhone());
        tDepart.setText(teacherModel.getDepartment());

        String[] batch = {"42","43","44","45","46","47"};
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, batch);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tBatchSpin.setAdapter(adapter);

        TextView title = new TextView(getContext());
        title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tCoCoordLayout.addView(title);

        new FirebaseDatabaseHelper().getCourseCoordinator(teacherModel.getId(), model -> {
            title.setText("Batch : "+model.getBatch());
        });

        coordinatorAddBtn.setOnClickListener(v -> {
            title.setText("Batch : "+tBatchSpin.getSelectedItem());
            new FirebaseDatabaseHelper().addCourseCoordinator(new CoordinatorModel(teacherModel.getId(), tBatchSpin.getSelectedItem().toString()));
        });

    }
}