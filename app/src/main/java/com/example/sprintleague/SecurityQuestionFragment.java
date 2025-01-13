package com.example.sprintleague;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SecurityQuestionFragment extends Fragment {


    private PageNavigationCallback callback;

    private EditText answer_EditText;
    private TextView question_TextView, valid_answer,wrong_answer;
    private RelativeLayout check;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PageNavigationCallback) {
            callback = (PageNavigationCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PageNavigationCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.secuirty_question_fragment, container, false);

        answer_EditText = view.findViewById(R.id.enter_answer_edittext_answer);

        question_TextView = view.findViewById(R.id.enter_answer_textview_question);
        valid_answer = view.findViewById(R.id.text_view_enter_valid_answer);
        wrong_answer = view.findViewById(R.id.enter_answer_wrong_answer);

        check = view.findViewById(R.id.enter_answer_click_check);

        question_TextView.setText(ForgotPasswordActivity.user.getSecurityQuestion());


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()){
                    checkAnswer();
                }
            }
        });
        return view;
    }


    private boolean isInputValid(){
        String answer = answer_EditText.getText().toString();

        if(answer.isEmpty()){

            valid_answer.setVisibility(View.VISIBLE);
            return false;
        }else{
            valid_answer.setVisibility(View.GONE);
            return true;
        }
    }

    private void checkAnswer(){
        String answer = answer_EditText.getText().toString();

        if(answer.equals(ForgotPasswordActivity.user.getSecurityQuestionResponse())){
            wrong_answer.setVisibility(View.INVISIBLE);
            callback.onValidationSuccess();
        }else{
            wrong_answer.setVisibility(View.VISIBLE);
        }
    }

}
