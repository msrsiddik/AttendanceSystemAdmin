package msr.attend.admin.Student;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private EditText studentName, studentId, studentBatch, studentPhone, guardianPhone;
    private Spinner departSelect;
    private Button stSubmitBtn, back;
    private FragmentInterface fragmentInterface;
    private ArrayAdapter<CharSequence> spinnerAdapter;

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
        studentBatch = view.findViewById(R.id.studentBatch);
        studentPhone = view.findViewById(R.id.studentPhone);
        guardianPhone = view.findViewById(R.id.guardianPhone);
        stSubmitBtn = view.findViewById(R.id.studentSubmitBtn);
        back = view.findViewById(R.id.addStudentFormBack);

        fragmentInterface = (FragmentInterface) getActivity();

        back.setOnClickListener(v -> {
            getFragmentManager().popBackStack("StudentList", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentInterface.student();
        });

        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.department_name, android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departSelect.setAdapter(spinnerAdapter);

        stSubmitBtn.setOnClickListener(v -> {
            String name = studentName.getText().toString();
            String depart = departSelect.getSelectedItem().toString();
            String stBatch = studentBatch.getText().toString();
            String stId = studentId.getText().toString();
            String stPhone = studentPhone.getText().toString();
            String gdPhone = guardianPhone.getText().toString();
            if (!name.equals("") && !depart.equals("") && !stId.equals("") && !studentBatch.equals("")) {
                StudentModel studentModel = new StudentModel(name, depart, stId, stBatch, stPhone, gdPhone);
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

        Bundle bundle = this.getArguments();
        if (bundle!=null){
            setUpEdittableStudent(bundle);
        }
    }

    private void setUpEdittableStudent(Bundle bundle){
        String id = bundle.getString("id");
        studentName.setText(bundle.getString("name"));
        studentId.setText(bundle.getString("studentId"));
        stSubmitBtn.setText("Update");
        departSelect.setSelection(spinnerAdapter.getPosition(bundle.getString("depart")));
        studentBatch.setText(bundle.getString("batch"));

        stSubmitBtn.setOnClickListener(v -> {
            new FirebaseDatabaseHelper().editStudent(new StudentModel(id, studentName.getText().toString(),
                    departSelect.getSelectedItem().toString(), studentId.getText().toString(), studentBatch.getText().toString(),
                    studentPhone.getText().toString(), guardianPhone.getText().toString()),
                    new FireMan.StudentDataShort() {
                @Override
                public void studentIsLoaded(List<StudentModel> students) {

                }

                @Override
                public void studentIsInserted() {

                }

                @Override
                public void studentIsDeleted() {

                }

                @Override
                public void studentIsEdited() {
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

}