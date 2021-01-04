package msr.attend.admin.Student;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import msr.attend.admin.FireMan;
import msr.attend.admin.FirebaseDatabaseHelper;
import msr.attend.admin.FragmentInterface;
import msr.attend.admin.Model.StudentModel;
import msr.attend.admin.R;

public class Student extends Fragment implements SearchView.OnQueryTextListener{
    private SearchView searchView;
    private ListView studentViewList;
    private FloatingActionButton addStudentBtn;
    private FragmentInterface fragmentInterface;
    private List<StudentModel> studentModelList = null;
    private StudentsAdapter studentsAdapter;

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
        searchView = view.findViewById(R.id.searchStudent);
        studentViewList = view.findViewById(R.id.studentsView);
        addStudentBtn = view.findViewById(R.id.addStudentBtn);

        getActivity().setTitle("Student List");

        loadStudentFromDb();

        fragmentInterface = (FragmentInterface) getActivity();
        addStudentBtn.setOnClickListener(v -> fragmentInterface.addStudentForm());
        registerForContextMenu(studentViewList);

//        searchView.setOnQueryTextListener(this);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        loadStudentFromDb();
//    }

    private void loadStudentFromDb(){
        new FirebaseDatabaseHelper().getStudents(new FireMan.StudentDataShort() {
            @Override
            public void studentIsLoaded(List<StudentModel> students) {
                if (getActivity()!=null) {
                    studentModelList = students;
                    studentsAdapter = new StudentsAdapter(getContext(), students);
                    studentViewList.setAdapter(studentsAdapter);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<StudentModel> list = new ArrayList<>();
        for (StudentModel student : studentModelList){
            if (student.toString().contains(newText)){
                list.add(student);
            }
        }

        ((StudentsAdapter)studentViewList.getAdapter()).update(list);

        return false;
    }

    class StudentsAdapter extends ArrayAdapter<StudentModel> {
        Context context;
        List<StudentModel> list = null;
        public StudentsAdapter(@NonNull Context context, @NonNull List<StudentModel> objects) {
            super(context, R.layout.student_row, objects);
            this.context = context;
            this.list = objects;
        }

        public void update(List<StudentModel> list) {
            this.list = new ArrayList<>();
            this.list.addAll(list);
            notifyDataSetChanged();
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