package msr.attend.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static msr.attend.admin.FireMan.ADMIN_AUTH;

public class Login extends Fragment {
    private EditText email, pass;
    private Button loginBtn;
    private FragmentInterface fragmentInterface;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        email = view.findViewById(R.id.loginEmail);
        pass = view.findViewById(R.id.loginPass);
        loginBtn = view.findViewById(R.id.signInBtn);

        getActivity().setTitle("Admin Login");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADMIN_AUTH.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "Successful Login", Toast.LENGTH_SHORT).show();
                                    fragmentInterface = (FragmentInterface) getActivity();
                                    fragmentInterface.dashboard();
                                } else {
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}