package msr.attend.admin;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msr.attend.admin.Model.ClassModel;
import msr.attend.admin.Model.CoordinatorModel;
import msr.attend.admin.Model.StudentModel;
import msr.attend.admin.Model.TeacherModel;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase database;
    private DatabaseReference teacherRef;
    private DatabaseReference studentRef;
    private DatabaseReference coordinatorRef;
    private DatabaseReference studentProfileRef;
    private DatabaseReference classInfoRef;
    private DatabaseReference superUserPermission;
    private List<TeacherModel> teachers = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        database = FirebaseDatabase.getInstance();
        teacherRef = database.getReference().child("Teachers");
        studentRef = database.getReference().child("Students");
        coordinatorRef = database.getReference().child("Coordinators");
        studentProfileRef = database.getReference().child("StudentsProfile");
        classInfoRef = database.getReference().child("ClassInformation");
        superUserPermission = database.getReference().child("SuperPermission");
    }

    public void deleteSuperSelectUser(String id, Context context){
        superUserPermission.child("SuperUser").child(id).setValue(null)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show());
    }

    public interface SuperUserListener{
        void superTeacher(List<String> teacherIdList);
    }

    public void getSuperSelectedTeacher(final SuperUserListener superUserListener){
        superUserPermission.child("SuperUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    list.add(child.getKey());
                }
                superUserListener.superTeacher(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setSuperSelectTeacher(String teacherId, String name){
        superUserPermission.child("SuperUser").child(teacherId).setValue(name);
    }

    public interface RoutineMode{
        void routineModeListener(String mode);
    }

    public void routineGetMode(final RoutineMode routineMode){
        superUserPermission.child("Routine").child("EveryoneSetup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mode = snapshot.getValue(String.class);
                routineMode.routineModeListener(mode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void routineSetMode(String mode){
        superUserPermission.child("Routine").child("EveryoneSetup").setValue(mode);
    }

    public void getClassInfo(String teacherId, FireMan.ClassInfoListener listener){
        List<ClassModel> list = new ArrayList<>();
        classInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    if (ds.exists()){
                        ClassModel classModel = ds.getValue(ClassModel.class);
                        if (classModel.getTeacherId().equals(teacherId)) {
                            list.add(classModel);
                        }
                    }
                }
                listener.classInfoIsLoaded(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void insertClassInfo(ClassModel classModel, final FireMan.ClassInfoListener listener){
        String id = classInfoRef.push().getKey();
        classModel.setClassId(id);
        classInfoRef.child(id).setValue(classModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.classInfoIsInserted();
            }
        });
    }

    public void deleteClassInfo(String classId, final FireMan.ClassInfoListener listener){
        classInfoRef.child(classId).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.classInfoIsDeleted();
            }
        });
    }

    public void getCourseCoordinator(String id, final FireMan.CoordinatorListener listener){
        coordinatorRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CoordinatorModel> coordinatorModels = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CoordinatorModel model = ds.getValue(CoordinatorModel.class);
                        coordinatorModels.add(model);
                    }
                }
                listener.coordinatorIsLoaded(coordinatorModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addCourseCoordinator(CoordinatorModel model){
        coordinatorRef.child(model.getId()).child('-'+model.getBatch()).setValue(model);
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

    public void getStudents(final FireMan.StudentDataShort dataShort){
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

    public void getAllRunningBatch(final FireMan.RunningBatchShot batchShot){
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> list = new HashSet<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    StudentModel model = ds.getValue(StudentModel.class);
                    list.add(model.getBatch());
                }
                batchShot.batchListener(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllCoordinateBatch(final FireMan.AlreadyCoordinateBatch listener){
        coordinatorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot tId : snapshot.getChildren()){
                    for (DataSnapshot ds : tId.getChildren()){
                        CoordinatorModel model = ds.getValue(CoordinatorModel.class);
                        list.add(model.getBatch());
                    }
                }
                listener.batchListener(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
