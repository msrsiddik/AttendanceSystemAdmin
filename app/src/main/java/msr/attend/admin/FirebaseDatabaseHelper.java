package msr.attend.admin;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import msr.attend.admin.Model.CoordinatorModel;
import msr.attend.admin.Model.StudentModel;
import msr.attend.admin.Model.TeacherModel;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase database;
    private DatabaseReference teacherRef;
    private DatabaseReference studentRef;
    private DatabaseReference coordinatorRef;
    private DatabaseReference studentProfileRef;
    private List<TeacherModel> teachers = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        database = FirebaseDatabase.getInstance();
        teacherRef = database.getReference().child("Teachers");
        studentRef = database.getReference().child("Students");
        coordinatorRef = database.getReference().child("Coordinators");
        studentProfileRef = database.getReference().child("StudentsProfile");
    }

    public void getCourseCoordinator(String id,FireMan.CoordinatorListener listener){
        coordinatorRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CoordinatorModel model = snapshot.getValue(CoordinatorModel.class);
                    listener.coordinatorIsLoad(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addCourseCoordinator(CoordinatorModel model){
        coordinatorRef.child(model.getId()).setValue(model);
    }

    public void editStudent(StudentModel model, final FireMan.StudentDataShort dataShort){
        studentRef.child(model.getId()).setValue(model)
                .addOnSuccessListener(aVoid -> {
                    dataShort.studentIsEdited();
                });
    }

    public void deleteStudent(String id, final FireMan.StudentDataShort dataShort){
        studentRef.child(id).setValue(null)
                .addOnSuccessListener(aVoid -> dataShort.studentIsDeleted());
    }

    public void getStudents(FireMan.StudentDataShort dataShort){
        List<StudentModel> list = new ArrayList<>();
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    StudentModel model = ds.getValue(StudentModel.class);
                    list.add(model);
                }
                dataShort.studentIsLoaded(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void insertStudent(StudentModel student, final FireMan.StudentDataShort dataShort){
        String id = studentRef.push().getKey();
        student.setId(id);
        studentRef.child(student.getId()).setValue(student)
                .addOnSuccessListener(aVoid -> dataShort.studentIsInserted());
    }

    public void editTeacher(TeacherModel teacher, final FireMan.TeacherDataShort dataShort){
        teacherRef.child(teacher.getId()).setValue(teacher)
                .addOnSuccessListener(aVoid -> dataShort.teacherIsEdited());
    }

    public void deleteTeacher(String id, final FireMan.TeacherDataShort dataShort){
        teacherRef.child(id).setValue(null)
                .addOnSuccessListener(aVoid -> dataShort.teacherIsDelete());
    }

    public void addTeacher(TeacherModel teacher) {
        String key = teacherRef.push().getKey();
        teacher.setId(key);
        teacherRef.child(key).setValue(teacher);
    }

    public void getTeachers(final FireMan.TeacherDataShort dataShort) {
        List<TeacherModel> list = new ArrayList<>();
        teacherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    TeacherModel model = ds.getValue(TeacherModel.class);
                    list.add(model);
                }
                dataShort.teacherIsLoaded(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
