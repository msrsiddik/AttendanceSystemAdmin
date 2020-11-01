package msr.attend.admin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireMan {
    public final static FirebaseAuth ADMIN_AUTH = FirebaseAuth.getInstance();

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public final static DatabaseReference TEACHER_DATABASE_REFERENCE = database.getReference("Teachers");
}
