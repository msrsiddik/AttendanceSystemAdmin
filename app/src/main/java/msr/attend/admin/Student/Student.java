package msr.attend.admin.Student;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import msr.attend.admin.FireMan;
import msr.attend.admin.FirebaseDatabaseHelper;
import msr.attend.admin.FragmentInterface;
import msr.attend.admin.Model.StudentModel;
import msr.attend.admin.R;

public class Student extends Fragment {
    private ListView studentViewList;
    private FloatingActionButton addStudentBtn;
    private FragmentInterface fragmentInterface;
    private List<StudentModel> studentModelList = new ArrayList<>();

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
        studentViewList = view.findViewById(R.id.studentsView);
        addStudentBtn = view.findViewById(R.id.addStudentBtn);

        loadStudentFromDb();

        fragmentInterface = (FragmentInterface) getActivity();
        addStudentBtn.setOnClickListener(v -> fragmentInterface.addStudentForm());

    }

    @Override
    public void onStart() {
        super.onStart();
        loadStudentFromDb();
    }

    private void loadStudentFromDb(){
        new FirebaseDatabaseHelper().getStudents(new FireMan.StudentDataShort() {
            @Override
            public void studentIsLoaded(List<StudentModel> students) {
                if (getActivity()!=null) {
                    studentViewList.setAdapter(new StudentsAdapter(getContext(), students));
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

    class StudentsAdapter extends ArrayAdapter<StudentModel>{
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