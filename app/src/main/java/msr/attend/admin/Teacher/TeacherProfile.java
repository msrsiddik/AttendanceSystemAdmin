package msr.attend.admin.Teacher;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import msr.attend.admin.FireMan;
import msr.attend.admin.FirebaseDatabaseHelper;
import msr.attend.admin.Model.ClassModel;
import msr.attend.admin.Model.CoordinatorModel;
import msr.attend.admin.Model.TeacherModel;
import msr.attend.admin.R;
import msr.attend.admin.gson.Utils;

public class TeacherProfile extends Fragment {
    private TextView tName, tEmail, tPhone, tDepart;
    private LinearLayout tCoCoordLayout;
    private Spinner tBatchSpin;
    private Button coordinatorAddBtn, addClassBtn;
    private TeacherModel teacherModel;
    private ListView classListView;
    private String teacherId;

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
        addClassBtn = view.findViewById(R.id.addClassBtn);
        classListView = view.findViewById(R.id.classListView);

        Bundle bundle = getArguments();
        teacherModel = Utils.getGsonParser().fromJson(bundle.getString("teacher"),TeacherModel.class);
        tName.setText(teacherModel.getName());
        tEmail.setText(teacherModel.getEmail());
        tPhone.setText(teacherModel.getPhone());
        tDepart.setText(teacherModel.getDepartment());

        teacherId = teacherModel.getId();

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

        addClassBtn.setOnClickListener(v -> setUpAddClass());

        setUpClassInfo();
    }

    private void setUpClassInfo(){
        new FirebaseDatabaseHelper().getClassInfo(teacherId, new FireMan.ClassInfoListener() {
            @Override
            public void classInfoIsInserted() {

            }

            @Override
            public void classInfoIsLoaded(List<ClassModel> classModelList) {
                if (getActivity()!=null) {
                    ClassAdapter adapter = new ClassAdapter(getContext(), classModelList);
                    classListView.setAdapter(adapter);
                }
            }

            @Override
            public void classInfoIsEdited() {

            }

            @Override
            public void classInfoIsDeleted() {

            }
        });
    }

    class ClassAdapter extends ArrayAdapter<ClassModel>{
        Context context;
        List<ClassModel> list;

        public ClassAdapter(@NonNull Context context, @NonNull List<ClassModel> objects) {
            super(context, R.layout.class_info_row, objects);
            this.context = context;
            list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.class_info_row, parent, false);

            TextView courseCode = view.findViewById(R.id.courseCode);
            courseCode.setText(list.get(position).getSubCode());
            TextView classBatch = view.findViewById(R.id.classBatch);
            classBatch.setText(list.get(position).getBatch());
            TextView classTime = view.findViewById(R.id.classTime);
            classTime.setText(list.get(position).getTime());

            return view;
        }
    }

    private void setUpAddClass(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_class);

        Spinner cDepart = dialog.findViewById(R.id.classDepart);
        Spinner cBatch = dialog.findViewById(R.id.classBatch);
        Spinner cSemester = dialog.findViewById(R.id.classSemester);
        Spinner cSubCode = dialog.findViewById(R.id.classSubjectCode);
        Spinner cDay = dialog.findViewById(R.id.classDay);
        EditText cTime = dialog.findViewById(R.id.classTime);
        Button cSubmitBtn = dialog.findViewById(R.id.classSubmitBtn);

        ArrayAdapter<CharSequence> departAdapter = ArrayAdapter.createFromResource(getContext(), R.array.department_name, android.R.layout.simple_spinner_item);
        departAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cDepart.setAdapter(departAdapter);

        ArrayAdapter<CharSequence> batchAdapter = ArrayAdapter.createFromResource(getContext(), R.array.batch, android.R.layout.simple_spinner_item);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cBatch.setAdapter(batchAdapter);

        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(getContext(), R.array.semester, android.R.layout.simple_spinner_item);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cSemester.setAdapter(semesterAdapter);

        int[] subCodeBySemester = {R.array.first_bsc_cse, R.array.second_bsc_cse, R.array.third_bsc_cse, R.array.fourth_bsc_cse,
                R.array.fifth_bsc_cse, R.array.sixth_bsc_cse, R.array.seventh_bsc_cse, R.array.eighth_bsc_cse, R.array.ninth_bsc_cse,
                R.array.tenth_bsc_cse, R.array.eleventh_bsc_cse, R.array.twelfth_bsc_cse};

        cSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> subCodeAdapter = ArrayAdapter.createFromResource(getContext(), subCodeBySemester[position+1], android.R.layout.simple_spinner_item);
                subCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cSubCode.setAdapter(subCodeAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.dayName, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cDay.setAdapter(dayAdapter);

        cTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    cTime.setText(hourOfDay+" : "+minute);
                }
            }, 12, 00, true);
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });

        cSubmitBtn.setOnClickListener(v -> {
            if ( cDepart.getSelectedItemPosition() != 0
                    && cBatch.getSelectedItemPosition() != 0
                    && cSubCode.getSelectedItemPosition() != 0
                    && cDay.getSelectedItemPosition() != 0 ) {
                String depart = cDepart.getSelectedItem().toString();
                String batch = cBatch.getSelectedItem().toString();
                String semester = cSemester.getSelectedItem().toString();
                String subCode = cSubCode.getSelectedItem().toString();
                String day = cDay.getSelectedItem().toString();
                String time = cTime.getText().toString();
                ClassModel classModel = new ClassModel(teacherId, depart, batch, semester, subCode, day, time);
                new FirebaseDatabaseHelper().insertClassInfo(classModel, new FireMan.ClassInfoListener() {
                    @Override
                    public void classInfoIsInserted() {
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void classInfoIsLoaded(List<ClassModel> classModelList) {

                    }

                    @Override
                    public void classInfoIsEdited() {

                    }

                    @Override
                    public void classInfoIsDeleted() {

                    }
                });
            } else {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}