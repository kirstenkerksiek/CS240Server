package com.example.family_map_client;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.Executors;

import DataManagement.DataCache;
import Net.LoginAsyncTask;
import Net.RegisterAsyncTask;
import Requests.LoginRequest;
import Requests.RegisterRequest;

public class LoginFragment extends Fragment {
    EditText hostText;
    EditText portText;
    EditText usernameText;
    EditText passwordText;
    EditText firstText;
    EditText lastText;
    EditText emailText;
    RadioButton maleButton;
    RadioButton femaleButton;
    Button register;
    Button login;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hostText = view.findViewById(R.id.host);
        portText = view.findViewById(R.id.port);
        usernameText = view.findViewById(R.id.username);
        passwordText = view.findViewById(R.id.password);
        firstText = view.findViewById(R.id.first);
        lastText = view.findViewById(R.id.last);
        emailText = view.findViewById(R.id.email);
        maleButton = view.findViewById(R.id.male);
        femaleButton = view.findViewById(R.id.female);
        maleButton.setChecked(true);
        login = view.findViewById(R.id.login);
        register = view.findViewById(R.id.register);

        login.setEnabled(false);
        register.setEnabled(false);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //create asyncLogin
                DataCache cache = DataCache.getInstance();

                LoginRequest request = new LoginRequest(); //registerRequest
                request.setUsername(usernameText.getText().toString());
                request.setPassword(passwordText.getText().toString());
                LoginAsyncTask task = new LoginAsyncTask(hostText.getText().toString(), portText.getText().toString(), getActivity());
                //task.execute(request);
                task.executeOnExecutor(Executors.newScheduledThreadPool(1), request);
            }
        });

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //create asyncRegister
                DataCache cache = DataCache.getInstance();

                RegisterRequest request = new RegisterRequest(); //registerRequest
                request.setUsername(usernameText.getText().toString());
                request.setPassword(passwordText.getText().toString());
                request.setFirstName(firstText.getText().toString());
                request.setLastName(lastText.getText().toString());
                request.setEmail(emailText.getText().toString());
                if (maleButton.isChecked()) {
                    request.setGender("m");
                }
                else if (femaleButton.isChecked()) {
                    request.setGender("f");
                }

                RegisterAsyncTask task = new RegisterAsyncTask(hostText.getText().toString(), portText.getText().toString(), getActivity());
                task.execute(request);
            }
        });
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(hostText.getText().toString().matches("") ) {
                    login.setEnabled(false);
                    register.setEnabled(false);
                }
                else if(portText.getText().toString().matches("") ) {
                    login.setEnabled(false);
                    register.setEnabled(false);
                }
                else if(usernameText.getText().toString().matches("") ) {
                    login.setEnabled(false);
                    register.setEnabled(false);
                }
                else if(usernameText.getText().toString().matches("") ) {
                    login.setEnabled(false);
                    register.setEnabled(false);
                }
                else if(passwordText.getText().toString().matches("") ) {
                    login.setEnabled(false);
                    register.setEnabled(false);
                }
                else if(firstText.getText().toString().matches("") ) {
                    login.setEnabled(true);
                    register.setEnabled(false);
                }
                else if(lastText.getText().toString().matches("") ) {
                    login.setEnabled(true);
                    register.setEnabled(false);
                }
                else if(emailText.getText().toString().matches("") ) {
                    login.setEnabled(true);
                    register.setEnabled(false);
                }
                else {
                    login.setEnabled(true);
                    register.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        hostText.addTextChangedListener(watcher);
        portText.addTextChangedListener(watcher);
        usernameText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);
        firstText.addTextChangedListener(watcher);
        lastText.addTextChangedListener(watcher);
        emailText.addTextChangedListener(watcher);
    }


}
