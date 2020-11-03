package msr.attend.admin.Student;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import msr.attend.admin.FireMan;
import msr.attend.admin.FirebaseDatabaseHelper;
import msr.attend.admin.FragmentInterface;
import msr.attend.admin.Model.StudentModel;
import msr.attend.admin.R;

public class AddStudent extends Fragment {
    private EditText studentName, studentId;
    private Spinner departSelect;
    private Button stSubmitBtn;
    private FragmentInterface fragmentInterface;

    public AddStudent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        studentName = view.findViewById(R.id.studentName);
        departSelect = view.findViewById(R.id.studentDepart);
        studentId = view.findViewById(R.id.studentId);
        stSubmitBtn = view.findViewById(R.id.studentSubmitBtn);

        fragmentInterface = (FragmentInterface) getActivity();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.department_name, android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departSelect.setAdapter(spinnerAdapter);

        stSubmitBtn.setOnClickListener(v -> {
            String name = studentName.getText().toString();
            String depart = departSelect.getSelectedItem().toString();
            String stId = studentId.getText().toString();
            if (!name.equals("") && !depart.equals("") && !stId.equals("")) {
                StudentModel studentModel = new StudentModel(name, depart, stId);
                new FirebaseDatabaseHelper().insertStudent(studentModel, new FireMan.StudentDataShort() {
                    @Override
                    public void studentIsLoaded(List<StudentModel> students) {

                    }

                    @Override
                    public void studentIsInserted() {
                        Toast.makeText(getContext(), "Student Inserted", Toast.LENGTH_SHORT).show();
                        studentName.getText().clear();
                        studentId.getText().clear();
                    }

                    @Override
                    public void studentIsDeleted() {

                    }

                    @Override
                    public void studentIsEdited() {

                    }
                });
            } else {
                Toast.makeText(getContext(), "Fill up all field", Toast.LENGTH_SHORT).show();
            }
        });
    }

}