package com.loterh.robert.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Robert Loterh,
 * on 2/26/2020, @ 12:26 PM
 **/
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private  final Context mContext;
    private final List<NoteInfo> mNoteInfos; //to hold list of notes
    private final LayoutInflater mLayoutInflater;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> noteInfos) {
        mContext = context;
        mNoteInfos = noteInfos;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //creates ViewHolder instances & create the views
        //or inflates the view and stores information about that viewHoler
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false); // flase: don't automatically attach view to parent
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //responsible for associating data with views
        NoteInfo note = mNoteInfos.get(position);
        holder.mTextCourse.setText(note.getCourse().getTitle());
        holder.mTextTitle.setText(note.getTitle());
        holder.mCurrentPosition = position;

    }

    @Override
    public int getItemCount() { //Number of data items present
        return mNoteInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourse;
        public final TextView mTextTitle;
        public int mCurrentPosition; // to hold position of current selected item, not final

        public ViewHolder(@NonNull View itemView) {
            //This constructor is called just after the view is inflated
            super(itemView);

            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NoteActivity.class);// Note:: mContext
                    intent.putExtra(NoteActivity.NOTE_POSITION, mCurrentPosition);
                    mContext.startActivity(intent);
                }
            });

        }
    }
}
