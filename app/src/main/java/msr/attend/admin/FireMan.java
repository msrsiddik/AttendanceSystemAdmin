package msr.attend.admin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Set;

import msr.attend.admin.Model.ClassModel;
import msr.attend.admin.Model.CoordinatorModel;
import msr.attend.admin.Model.StudentModel;
import msr.attend.admin.Model.TeacherModel;

public class FireMan {
    public final static FirebaseAuth ADMIN_AUTH = FirebaseAuth.getInstance();

    public interface TeacherDataShort{
        void teacherIsLoaded(List<TeacherModel> teachers);
        void teacherIsDelete();
        void teacherIsEdited();
    }

    public interface StudentDataShort{
        void studentIsLoaded(List<StudentModel> students);
        void studentIsInserted();
        void studentIsDeleted();
        void studentIsEdited();
    }

    public interface CoordinatorListener{
        void coordinatorIsLoaded(List<CoordinatorModel> models);
    }

    public interface ClassInfoListener {
        void classInfoIsInserted();
        void classInfoIsLoaded(List<ClassModel> classModelList);
        void classInfoIsEdited();
        void classInfoIsDeleted();
    }

    public interface RunningBatchShot {
        void batchListener(Set<String> batchs);
    }

    public interface AlreadyCoordinateBatch{
        void batchListener(List<String> coordinateBatchs);
    }
}
