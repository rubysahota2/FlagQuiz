package com.example.flagquiz;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    Set<String> regionSet;
    ArrayList<String> fileNameList = new ArrayList<String>();
    LinearLayout[] guessLinearLayout;
    Random random = new Random();

    ImageView flagImageView;

    ArrayList<String> quizCountriesList = new ArrayList<>();

    int guessRows;

    String correctAnswer;

    TextView answerTextView;

    int completedQuestions = 0;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        guessLinearLayout = new LinearLayout[4];
        guessLinearLayout[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayout[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayout[2] = view.findViewById(R.id.row3LinearLayout);
        guessLinearLayout[3] = view.findViewById(R.id.row4LinearLayout);

        flagImageView = view.findViewById(R.id.flagImageView);

        for(LinearLayout linearLayout : guessLinearLayout){
            for(int i=0; i<2; i++){
                Button button = (Button) linearLayout.getChildAt(i);
                button.setOnClickListener(guessButtonListener);
            }

        }

        answerTextView = view.findViewById(R.id.answerTextView);

     return view;
    }

    public void updateGuessRows(SharedPreferences defaultSharedPreferences) {
        String choices = defaultSharedPreferences.getString(MainActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices)/2;

        for(LinearLayout linearLayout : guessLinearLayout){
            linearLayout.setVisibility(View.GONE);
        }

        for(int i=0; i<guessRows; i++)
        {
            guessLinearLayout[i].setVisibility(View.VISIBLE);
        }


    }

    public void updateRegions(SharedPreferences defaultSharedPreferences) {
        regionSet =  defaultSharedPreferences.getStringSet(MainActivity.REGIONS, null);
    }


    public void resetQuiz() {
        //get the flags according to the region set
        // create an array with names of ALL the flags in region set for ex - total 200
        // I would select 10 flags at random
        // I would create list of questions flags, before selecting a new random flag,
        // I would make sure new flag doesnot not exist in my current list of question flags

        AssetManager assets = getActivity().getAssets();
        for (String region : regionSet){
            try {
                // List of all the flag names for each region set
                String[] paths = assets.list(region);
                for(String path : paths){
                    fileNameList.add(path.replace(".png", ""));
                }

            } catch (IOException e) {
               Log.e("An exception occurred while loading the file names", e.getMessage());
            }
            int flagCounter = 1;
            while(flagCounter <= 10){
                int randomIndex =  random.nextInt(fileNameList.size());
                String fileName = fileNameList.get(randomIndex);

                if(!quizCountriesList.contains(fileName)){
                    quizCountriesList.add(fileName);
                    ++flagCounter;
                }

            }

        }

        loadNextFlag();

    }

    private void loadNextFlag() {
      answerTextView.setText("");
      enableButtons();
      String currentFLag = quizCountriesList.remove(0);
      String region = currentFLag.substring(0, currentFLag.indexOf('-')); // extracting Africa from Africa-Algeria

      AssetManager assets = getActivity().getAssets();
        try {
            InputStream stream =  assets.open(region + "/" + currentFLag + ".png"); // Africa/Africa-Algeria.png
            Drawable flag = Drawable.createFromStream(stream, currentFLag);
            flagImageView.setImageDrawable(flag);
        } catch (IOException e) {
            // Log the error
        }

        // Think about setting the answers
        //One of them has to be correct and others should be randomised from the fileNameList
        // Positioning of the correct answer has to be random as well

        Collections.shuffle(fileNameList);
        correctAnswer = currentFLag.replace(".png", "");
        fileNameList.remove(correctAnswer);

        for(int row=0; row<guessRows; row++){
            for(int column=0; column<2;column++){
                String filename = fileNameList.get((row*2) + column); // 0,1,2,3
                Button guessButton = (Button) guessLinearLayout[row].getChildAt(column);
                guessButton.setText(filename.substring(filename.indexOf('-')+1));

            }
        }

        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);

        Button correctButton = (Button) guessLinearLayout[row].getChildAt(column);
        correctAnswer = (correctAnswer.substring(correctAnswer.indexOf('-')+1));
        correctButton.setText(correctAnswer);

    }

    private void enableButtons() {
        for(int row =0; row<guessRows; row++){
            for(int col =0; col<2; col++){
                guessLinearLayout[row].getChildAt(col).setEnabled(true);
            }
        }
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button guessButton = (Button) view;
            String guess = (String) guessButton.getText();

            if(guess.equals(correctAnswer)){
                ++completedQuestions;
                answerTextView.setText(correctAnswer);
                answerTextView.setTextColor(Color.GREEN);
                disableButtons();

                if(completedQuestions == 10){
                    resetQuiz();


                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextFlag();
                    }
                }, 1000);
            }
            else{
                answerTextView.setText("Incorrect Answer");
                answerTextView.setTextColor(Color.RED);
                guessButton.setEnabled(false);
            }
        }
    };

    private void disableButtons() {
        for(int row =0; row<guessRows; row++){
            for(int col =0; col<2; col++){
                guessLinearLayout[row].getChildAt(col).setEnabled(false);
            }
        }
    }
}