package msr.attend.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

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
        fragmentManager.beginTransaction().replace(R.id.FragContainer, new Student()).addToBackStack(null).commit();
    }
}