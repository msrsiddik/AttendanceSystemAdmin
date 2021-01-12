package msr.attend.admin.Teacher;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import msr.attend.admin.FireMan;
import msr.attend.admin.FirebaseDatabaseHelper;
import msr.attend.admin.FragmentInterface;
import msr.attend.admin.Model.TeacherModel;
import msr.attend.admin.R;
import msr.attend.admin.gson.Utils;

public class Teacher extends Fragment {
    private ListView listView;
    private FloatingActionButton addTeacher;
    private FragmentInterface fragmentInterface;
    private String[] departmentList = null;
    private List<TeacherModel> teacherList = new ArrayList<>();
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

        getActivity().setTitle("All Teacher");

        departmentList = getResources().getStringArray(R.array.department_name);

        setUpListView();

        registerForContextMenu(listView);

        addTeacher.setOnClickListener(v -> addTheTeacher());
    }

    private void setUpListView() {
        new FirebaseDatabaseHelper().getTeachers(new FireMan.TeacherDataShort() {
            @Override
            public void teacherIsLoaded(List<TeacherModel> teachers) {
                teacherList.clear();
                teacherList = teachers;
                if (getActivity()!=null) {
                    MyAdapter adapter = new MyAdapter(getContext(), teachers);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Bundle bundle = new Bundle();
                        TeacherModel teacher = teacherList.get(position);
                        String tString = Utils.getGsonParser().toJson(teacher);
                        bundle.putString("teacher", tString);
                        TeacherProfile teacherProfile = new TeacherProfile();
                        teacherProfile.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.FragContainer, teacherProfile).addToBackStack(null).commit();
                    });
                }
            }

            @Override
            public void teacherIsDelete() {

            }

            @Override
            public void teacherIsEdited() {

            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.teacherListView){
            MenuInflater inflater = new MenuInflater(getContext());
            inflater.inflate(R.menu.menu_list, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if ("Edit".equals(title)) {
            editTeacher(teacherList.get(menuinfo.position));
        } else if ("Delete".equals(title)) {
            new FirebaseDatabaseHelper().deleteTeacher(teacherList.get(menuinfo.position).getId(),
                    new FireMan.TeacherDataShort() {
                @Override
                public void teacherIsLoaded(List<TeacherModel> teachers) {

                }

                @Override
                public void teacherIsDelete() {
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    setUpListView();
                }

                        @Override
                        public void teacherIsEdited() {

                        }
                    });
        } else {
            return false;
        }

        return true;
    }

    void editTeacher(final TeacherModel teacherModel){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_add_teacher);
        TextInputEditText nameEdit = dialog.findViewById(R.id.teacherName);
        TextInputEditText phoneEdit = dialog.findViewById(R.id.teacherPhoneNum);
        TextInputEditText emailEdit = dialog.findViewById(R.id.teacherEmail);
        TextInputEditText passEdit = dialog.findViewById(R.id.teacherPassword);
        Spinner departmentSelect = dialog.findViewById(R.id.departmentSelect);
        RadioGroup radioGroup = dialog.findViewById(R.id.teacherGenderRdG);
        Button signUp = dialog.findViewById(R.id.teacherSubmit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.department_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSelect.setAdapter(adapter);

        nameEdit.setText(teacherModel.getName());
        phoneEdit.setText(teacherModel.getPhone());
        emailEdit.setText(teacherModel.getEmail());
        passEdit.setText(teacherModel.getPassword());
        departmentSelect.setSelection(adapter.getPosition(teacherModel.getDepartment()));
        if (teacherModel.getGender().equals("Male")) {
            radioGroup.check(R.id.maleRd);
        } else if (teacherModel.getGender().equals("Female")) {
            radioGroup.check(R.id.femaleRd);
        }

        signUp.setOnClickListener(v -> {
            int selectGender = radioGroup.getCheckedRadioButtonId();
            RadioButton genderRdBtn = dialog.findViewById(selectGender);
            String departName = departmentSelect.getSelectedItem().toString();

            TeacherModel editableTeacher = new TeacherModel(teacherModel.getId(), nameEdit.getText().toString(),
                    phoneEdit.getText().toString(), emailEdit.getText().toString(), departName,
                    genderRdBtn.getText().toString(), passEdit.getText().toString());

            new FirebaseDatabaseHelper().editTeacher(editableTeacher, new FireMan.TeacherDataShort() {
                @Override
                public void teacherIsLoaded(List<TeacherModel> teachers) {

                }

                @Override
                public void teacherIsDelete() {

                }

                @Override
                public void teacherIsEdited() {
                    setUpListView();
                    Toast.makeText(getContext(), "Edited", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.cancel();
        });

        dialog.show();
    }

    private void addTheTeacher() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_add_teacher);

        TextInputEditText nameEdit = dialog.findViewById(R.id.teacherName);
        TextInputEditText phoneEdit = dialog.findViewById(R.id.teacherPhoneNum);
        TextInputEditText emailEdit = dialog.findViewById(R.id.teacherEmail);
        TextInputEditText passEdit = dialog.findViewById(R.id.teacherPassword);
        Spinner departmentSelect = dialog.findViewById(R.id.departmentSelect);
        RadioGroup radioGroup = dialog.findViewById(R.id.teacherGenderRdG);
        Button signUp = dialog.findViewById(R.id.teacherSubmit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.department_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSelect.setAdapter(adapter);

        signUp.setOnClickListener(v -> {
            int selectGender = radioGroup.getCheckedRadioButtonId();
            RadioButton genderRdBtn = dialog.findViewById(selectGender);

            TeacherModel teacherModel = new TeacherModel(nameEdit.getText().toString(),
                    phoneEdit.getText().toString(), emailEdit.getText().toString(),
                    departmentSelect.getSelectedItem().toString(), genderRdBtn.getText().toString(),
                    passEdit.getText().toString());

            if (!teacherModel.getName().equals("") && !teacherModel.getPhone().equals("") && !teacherModel.getEmail().equals("")
                    && !teacherModel.getGender().equals("") && !teacherModel.getPassword().equals("")){

                new FirebaseDatabaseHelper().addTeacher(teacherModel);
                setUpListView();
                dialog.cancel();

            } else {
                Toast.makeText(getContext(), "Fill up all field", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    class MyAdapter extends ArrayAdapter<TeacherModel>{
        Context context;
        List<TeacherModel> list = null;

        public MyAdapter(@NonNull Context context, @NonNull List<TeacherModel> objects) {
            super(context, R.layout.teacher_list_item, objects);
            this.context = context;
            this.list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.teacher_list_item, parent, false);
            TextView name = row.findViewById(R.id.teacherNameView);
            TextView depart = row.findViewById(R.id.teacherDepartView);

            name.setText(list.get(position).getName());
            depart.setText(list.get(position).getDepartment());
            return row;
        }
    }

}