package msr.attend.admin.Student;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msr.attend.admin.FireMan;
import msr.attend.admin.FirebaseDatabaseHelper;
import msr.attend.admin.FragmentInterface;
import msr.attend.admin.Model.StudentModel;
import msr.attend.admin.R;

public class Student extends Fragment {
    private Spinner selectBatch, selectDepart;
    private ListView studentViewList;
    private FloatingActionButton addStudentBtn;
    private FragmentInterface fragmentInterface;
    private List<StudentModel> studentModelList = null;
    private StudentsAdapter studentsAdapter;

    private boolean setSpinnerItem = true;

    private Set<String> departmentList = new HashSet<>();
    private Set<String> batchList = new HashSet<>();

    public Student() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        selectDepart = view.findViewById(R.id.selectDepart);
        selectBatch = view.findViewById(R.id.selectBatch);
        studentViewList = view.findViewById(R.id.studentsView);
        addStudentBtn = view.findViewById(R.id.addStudentBtn);

//        getActivity().setTitle("Student List");

        setSpinnerItem = true;
        loadStudentFromDb("","");

        fragmentInterface = (FragmentInterface) getActivity();
        addStudentBtn.setOnClickListener(v -> fragmentInterface.addStudentForm());
        registerForContextMenu(studentViewList);

        selectDepart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String depart = selectDepart.getSelectedItem().toString();
                if (!depart.equals("Select Depart")) {
                    loadStudentFromDb(depart, "");
                } else {
                    loadStudentFromDb("","");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String depart = selectDepart.getSelectedItem().toString();
                String batch = selectBatch.getSelectedItem().toString();
                if (!depart.equals("Select Depart") && !batch.equals("Select Batch")) {
                    loadStudentFromDb(depart, batch);
                } else if (depart.equals("Select Depart")){
                    Toast.makeText(getContext(), "plz choose any department", Toast.LENGTH_SHORT).show();
                } else {
                    loadStudentFromDb("","");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        studentViewList.setOnItemClickListener((parent, view1, position, id) -> {
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
            dialog.setContentView(R.layout.student_profile);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextView studentName, studentBatch, studentDepart, studentNo, guardianNo;
            studentName = dialog.findViewById(R.id.studentName);
            studentBatch = dialog.findViewById(R.id.studentBatch);
            studentDepart = dialog.findViewById(R.id.studentDepart);
            studentNo = dialog.findViewById(R.id.studentNo);
            guardianNo = dialog.findViewById(R.id.guardianNo);

            StudentModel model = studentModelList.get(position);
            studentName.setText(model.getName());
            studentBatch.setText(model.getBatch());
            studentDepart.setText(model.getDepartment());
            studentNo.setText(model.getStudentPhone());
            guardianNo.setText(model.getGuardianPhone());

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        });
    }

    private void loadStudentFromDb(String depart, String batch){
        new FirebaseDatabaseHelper().getStudents(new FireMan.StudentDataShort() {
            @Override
            public void studentIsLoaded(List<StudentModel> students) {
                if (getActivity()!=null) {
                    if (setSpinnerItem) {
                        for (StudentModel student : students) {
                            departmentList.add(student.getDepartment());
                            batchList.add(student.getBatch());
                        }
                        spinnerSetup();
                        setSpinnerItem = false;
                    }

                    if (depart.isEmpty() && batch.isEmpty()) {
                        studentModelList = students;
                        getActivity().setTitle("Student List [ Total "+studentModelList.size()+" ]");
                        studentsAdapter = new StudentsAdapter(getContext(), students);
                        studentViewList.setAdapter(studentsAdapter);
                    } else if (!depart.isEmpty() && batch.isEmpty()){
                        List<StudentModel> selectedStudent = new ArrayList<>();
                        for (StudentModel student : students) {
                            if (student.getDepartment().equals(depart)){
                                selectedStudent.add(student);
                            }
                        }
                        studentModelList = selectedStudent;
                        getActivity().setTitle("Student List [ Total "+studentModelList.size()+" ]");
                        studentsAdapter = new StudentsAdapter(getContext(), selectedStudent);
                        studentViewList.setAdapter(studentsAdapter);
                    } else if (!depart.isEmpty() && !batch.isEmpty()){
                        List<StudentModel> selectedStudent = new ArrayList<>();
                        for (StudentModel student : students) {
                            if (student.getDepartment().equals(depart) && student.getBatch().equals(batch)){
                                selectedStudent.add(student);
                            }
                        }
                        studentModelList = selectedStudent;
                        getActivity().setTitle("Student List [ Total "+studentModelList.size()+" ]");
                        studentsAdapter = new StudentsAdapter(getContext(), selectedStudent);
                        studentViewList.setAdapter(studentsAdapter);
                    }
                }
            }

            @Override
            public void studentIsInserted() {

            }

            @Override
            public void studentIsDeleted() {

            }

            @Override
            public void studentIsEdited() {

            }
        });
    }

    private void spinnerSetup(){
        List<String> depart = new ArrayList<>(departmentList);
        depart.add(0,"Select Depart");
        ArrayAdapter<String> departAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, depart);
        selectDepart.setAdapter(departAdapter);

        List<String> batch = new ArrayList<>(batchList);
        batch.add(0,"Select Batch");
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, batch);
        selectBatch.setAdapter(batchAdapter);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.studentsView){
            MenuInflater inflater = new MenuInflater(getContext());
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if ("Edit".equals(title)) {
            StudentModel studentModel = studentModelList.get(menuinfo.position);
            AddStudent editStudent = new AddStudent();
            Bundle bundle = new Bundle();
            bundle.putString("id", studentModel.getId());
            bundle.putString("name", studentModel.getName());
            bundle.putString("depart", studentModel.getDepartment());
            bundle.putString("studentId", studentModel.getStudentId());
            bundle.putString("batch", studentModel.getBatch());
            editStudent.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.FragContainer, editStudent).addToBackStack(null).commit();

        } else if ("Delete".equals(title)) {
            new FirebaseDatabaseHelper().deleteStudent(studentModelList.get(menuinfo.position).getId(),
                    new FireMan.StudentDataShort() {
                        @Override
                        public void studentIsLoaded(List<StudentModel> students) {

                        }

                        @Override
                        public void studentIsInserted() {

                        }

                        @Override
                        public void studentIsDeleted() {
                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void studentIsEdited() {

                        }
                    });
        } else {
            return false;
        }

        return true;
    }

    class StudentsAdapter extends ArrayAdapter<StudentModel> {
        Context context;
        List<StudentModel> list = null;
        public StudentsAdapter(@NonNull Context context, @NonNull List<StudentModel> objects) {
            super(context, R.layout.student_row, objects);
            this.context = context;
            this.list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.student_row, parent, false);

            TextView name = view.findViewById(R.id.r_studentName);
            TextView stId = view.findViewById(R.id.r_studentId);
            name.setText(list.get(position).getName());
            stId.setText(list.get(position).getStudentId());
            return view;
        }

    }

}