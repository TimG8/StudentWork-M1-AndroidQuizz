package com.quizz.tguy.quizz;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

// EditQuizz : quizz edition menu available in the settings of the app
public class EditQuestion extends AppCompatActivity
{
    // View Model to access the Room
    private RoomViewModel mQuizzViewModel;
    private RoomQuizz rq;
    private int id;

    // onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editquestion_layout);

        // Get the current quizz with the intent extra
        mQuizzViewModel = ViewModelProviders.of(this).get(RoomViewModel.class);
        rq = mQuizzViewModel.getQuizzById(getIntent().getIntExtra("qid", 0));
        id = getIntent().getIntExtra("id", 0);

        // Set the title
        TextView title = findViewById(R.id.titleModifQuestion);
        title.append(" " + (id+1));

        final EditText question = findViewById(R.id.questionEdit);
        question.setText(rq.getQuestions(id));

        // Set the home button behaviour
        Button btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the main menu
                Intent int_home = new Intent(getApplicationContext(), MainMenu.class);
                int_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(int_home);
            }
        });

        Button btn_upQ = findViewById(R.id.btn_upQ);
        btn_upQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rq.setQuestionInAListOfQuestions(question.getText().toString(), id);
                mQuizzViewModel.update(rq);
            }
        });

        // Fill the main RecyclerView that contains the question list
        RecyclerView recyclerView = findViewById(R.id.questionsList);
        final EditQuestionRecyclerAdapter adapter = new EditQuestionRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe any modification in the question list and adapt the RecyclerView
        mQuizzViewModel = ViewModelProviders.of(this).get(RoomViewModel.class);
        mQuizzViewModel.getAllQuizz().observe(this, new Observer<List<RoomQuizz>>() {
            @Override
            public void onChanged(@Nullable final List<RoomQuizz> roomAllQuizz) {
                adapter.setAnswers(rq.getAnswers(id));
            }
        });
    }

    void setAnswers(String answer, int index) {
        rq.setAnswerInAListOfAnswer(answer, id, index);
        mQuizzViewModel.update(rq);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
