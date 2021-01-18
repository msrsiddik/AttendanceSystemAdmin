package msr.attend.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;

import msr.attend.admin.Student.AddStudent;
import msr.attend.admin.Student.Student;
import msr.attend.admin.Teacher.Teacher;

public class MainActivity extends AppCompatActivity implements FragmentInterface{

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        loginPage();
    }

    @Override
    public void loginPage() {
        Login login = new Login();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.FragContainer, login);
//        transaction.replace(R.id.FragContainer, new Dashboard());
        transaction.commit();
    }

    @Override
    public void dashboard() {
        fragmentManager.beginTransaction().replace(R.id.FragContainer, new Dashboard()).commit();
    }

    @Override
    public void teacher() {
        fragmentManager.beginTransaction().replace(R.id.FragContainer, new Teacher()).addToBackStack(null).commit();
    }

    @Override
    public void student() {
        fragmentManager.beginTransaction().replace(R.id.FragContainer, new Student()).addToBackStack("StudentList").commit();
    }

    @Override
    public void addStudentForm() {
        fragmentManager.beginTransaction().replace(R.id.FragContainer, new AddStudent()).addToBackStack(null).commit();
    }

    @Override
    public void superUserSet() {
        fragmentManager.beginTransaction().replace(R.id.FragContainer, new SuperUser()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount()>0){
            int fragments = fragmentManager.getBackStackEntryCount();
            if (fragments == 1) {
                fragmentManager.popBackStack();
            } else if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack();
            }
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.admin_panel);
            builder.setTitle("Admin App");
            builder.setMessage("Do you want to close this app?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    MainActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.show();
        }
    }
}