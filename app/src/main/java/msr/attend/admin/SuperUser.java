package msr.attend.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import msr.attend.admin.Model.TeacherModel;

public class SuperUser extends Fragment {
    private ToggleButton routineToggle;
    private Spinner teacherNameSpinner;
    private ListView permittedList;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private List<TeacherModel> teacherModels;
    private List<String> listViewItem = new ArrayList<>();

    public SuperUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_super_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        routineToggle = view.findViewById(R.id.routineToggle);
        teacherNameSpinner = view.findViewById(R.id.teacherNameSpinner);
        permittedList = view.findViewById(R.id.permittedList);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper();

        firebaseDatabaseHelper.routineGetMode(mode -> {
            Log.i("Mode", mode);
            if (!mode.isEmpty() && mode != null) {
                routineToggle.setChecked(Boolean.parseBoolean(mode));
            }
        });

        routineToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                firebaseDatabaseHelper.routineSetMode("true");
            } else {
                firebaseDatabaseHelper.routineSetMode("false");
            }
        });

        setupSpinner();

        teacherNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    TeacherModel teacher = teacherModels.get(position - 1);
                    firebaseDatabaseHelper.setSuperSelectTeacher(teacher.getId(), teacher.getName());
                }
                setupSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        firebaseDatabaseHelper.getSuperSelectedTeacher(teacherIdList -> {
            listViewItem.clear();
            for (String s : teacherIdList) {
                for (TeacherModel teacher : teacherModels) {
                    if (teacher.getId().equals(s)) {
                        listViewItem.add(teacher.getName());
                    }
                }
            }
            if (getActivity() != null) {
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listViewItem);
                permittedList.setAdapter(adapter);
            }
        });

        registerForContextMenu(permittedList);

    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Delete");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle().equals("Delete")) {
            for (TeacherModel teacher : teacherModels) {
                if (teacher.getName().equals(listViewItem.get(menuInfo.position))){
                    firebaseDatabaseHelper.deleteSuperSelectUser(teacher.getId(),getContext());
                }
            }
        }
        return super.onContextItemSelected(item);
    }

    private void setupSpinner() {
        firebaseDatabaseHelper.getTeachers(new FireMan.TeacherDataShort() {
            @Override
            public void teacherIsLoaded(List<TeacherModel> teachers) {
                teacherModels = teachers;
                List<String> list = new ArrayList<>();
                for (TeacherModel teacher : teachers) {
                    list.add(teacher.getName() + "   |   " + teacher.getDepartment());
                }
                list.add(0, "Select Teacher");
                if (getActivity() != null) {
                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
                    teacherNameSpinner.setAdapter(adapter);
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
}