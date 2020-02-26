package com.loterh.robert.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.loterh.robert.NoteTaker.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNoteInfo;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private NoteActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        mViewModel = viewModelProvider.get(NoteActivityViewModel.class);

        if(mViewModel.mIsNewlyCreated && savedInstanceState != null){mViewModel.restoreState(savedInstanceState);}

        mViewModel.mIsNewlyCreated = false;

        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        saveOriginalNoteValue();

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if (!mIsNewNote)
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

    }

    private void saveOriginalNoteValue() {
        if(mIsNewNote){return; }
        mViewModel.mOriginalNoteCourseId = mNoteInfo.getCourse().getCourseId();
        mViewModel.mOriginalNoteTitle = mNoteInfo.getTitle();
        mViewModel.mOriginalNoteText = mNoteInfo.getText();
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNoteInfo.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNoteInfo.getTitle());
        textNoteText.setText(mNoteInfo.getText());
    }

    //read object via parcel
    private void readDisplayStateValues() {
        Intent intent = getIntent();
        //mNoteInfo = intent.getParcelableExtra(NOTE_INFO); //Use that for Parcelable
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);// -1 indicates that the extra was not found in the intent
        mIsNewNote = position == POSITION_NOT_SET;
        if(mIsNewNote){
            createNewNote();
        }else{
            mNoteInfo = DataManager.getInstance().getNotes().get(position);
        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote(); //NOTE
        mNoteInfo = dm.getNotes().get(mNotePosition);
    }

    @Override
    protected void onPause() {//will be called when user is leaving that activity

        super.onPause();
        if(mIsCancelling){
            if(mIsNewNote){
                DataManager.getInstance().removeNote(mNotePosition);
            }else{
                storePreviousNoteValues();
            }
        }else {
            saveNote();
        }
    }

//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null){
            mViewModel.saveState(outState);
       }
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mNoteInfo.setCourse(course);
        mNoteInfo.setTitle(mViewModel.mOriginalNoteTitle);
        mNoteInfo.setText(mViewModel.mOriginalNoteText);
    }

    private void saveNote() { //save edited notes when leaving the screen
        mNoteInfo.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNoteInfo.setTitle(mTextNoteTitle.getText().toString()); //Note here
        mNoteInfo.setText(mTextNoteText.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }else if(id==R.id.action_cancel){
            mIsCancelling = true;
            finish();
        }else if(id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // determines what action you should take on a menu at given condition

        MenuItem item = menu.findItem(R.id.action_next);//select the appropraite menu item
        int lastNoteIndex = DataManager.getInstance().getNotes().size() -1;
        item.setEnabled(mNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote(); //save notes before moving to next note

        ++mNotePosition;
        mNoteInfo = DataManager.getInstance().getNotes().get(mNotePosition);

        saveOriginalNoteValue();
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

        invalidateOptionsMenu();//will make onPrepareOptionsMenu called again after each iteration
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Check what I learned in the Pluralsight course\"" +course.getTitle() + "\"\n" + mTextNoteText.getText();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822"); //mime type for email
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);

    }



}
