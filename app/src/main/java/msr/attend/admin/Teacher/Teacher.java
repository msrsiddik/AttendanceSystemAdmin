package msr.attend.admin.Teacher;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import msr.attend.admin.FragmentInterface;
import msr.attend.admin.Model.AddTeacherModel;
import msr.attend.admin.R;

import static msr.attend.admin.FireMan.TEACHER_DATABASE_REFERENCE;

public class Teacher extends Fragment {
    private ListView listView;
    private FloatingActionButton addTeacher;
    private FragmentInterface fragmentInterface;

    public Teacher() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.teacherListView);
        addTeacher = view.findViewById(R.id.addTeacher);
        fragmentInterface = (FragmentInterface) getActivity();


        addTeacher.setOnClickListener(v -> addTheTeacher());
    }

    private void addTheTeacher() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_add_teacher);

        EditText nameEdit = dialog.findViewById(R.id.teacherName);
        EditText phoneEdit = dialog.findViewById(R.id.teacherPhoneNum);
        EditText emailEdit = dialog.findViewById(R.id.teacherEmail);
        EditText passEdit = dialog.findViewById(R.id.teacherPassword);
        Spinner departmentSelect = dialog.findViewById(R.id.departmentSelect);
        RadioGroup radioGroup = dialog.findViewById(R.id.teacherGenderRdG);
        Button signUp = dialog.findViewById(R.id.teacherSubmit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.department_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSelect.setAdapter(adapter);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = TEACHER_DATABASE_REFERENCE.push().getKey();
                int selectGender = radioGroup.getCheckedRadioButtonId();
                RadioButton genderRdBtn = dialog.findViewById(selectGender);

                AddTeacherModel teacherModel = new AddTeacherModel(id, nameEdit.getText().toString(),
                        phoneEdit.getText().toString(), emailEdit.getText().toString(),
                        departmentSelect.getSelectedItem().toString(), genderRdBtn.getText().toString(),
                        passEdit.getText().toString());

                if (!teacherModel.getName().equals("") && !teacherModel.getPhone().equals("") && !teacherModel.getEmail().equals("")
                        && !teacherModel.getGender().equals("") && !teacherModel.getPassword().equals("")){

                    TEACHER_DATABASE_REFERENCE.child(departmentSelect.getSelectedItem().toString())
                            .child(id).setValue(teacherModel);
                    fragmentInterface.teacher();
                    dialog.cancel();

                } else {
                    Toast.makeText(getContext(), "Fill up all field", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

}