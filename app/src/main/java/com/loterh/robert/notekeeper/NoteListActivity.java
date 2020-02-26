package com.loterh.robert.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private ArrayAdapter<NoteInfo> mArrayAdapterNoteInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));

            }
        });
        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ArrayAdapter needs to know which dataset were changed
        mArrayAdapterNoteInfo.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
        // object marked as final enables it to be referenced inside an anonymous class
        final ListView listViewNotes = findViewById(R.id.lits_notes);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mArrayAdapterNoteInfo = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listViewNotes.setAdapter(mArrayAdapterNoteInfo);

        //When clicked send objects via parcel
        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
               //NoteInfo noteInfo = (NoteInfo) listViewNotes.getItemAtPosition(position);
                intent.putExtra(NoteActivity.NOTE_POSITION, position);
                startActivity(intent);
            }
        });
    }

}
