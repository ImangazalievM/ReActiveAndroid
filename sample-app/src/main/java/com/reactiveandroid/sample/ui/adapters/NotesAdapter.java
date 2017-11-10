package com.reactiveandroid.sample.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.models.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    public interface OnItemClickListener {
        void onNoteSelected(int position);
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy H:mm");

    private LayoutInflater layoutInflater;
    private List<Note> notes;
    private OnItemClickListener onItemClickListener;

    public NotesAdapter(Context context) {
        this.notes = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.itemView.setOnClickListener(view -> onNoteSelected(position));
        String noteLetter = !note.getTitle().isEmpty() ? note.getTitle().substring(0, 1) : "";
        TextDrawable noteDrawable = TextDrawable.builder().buildRound(noteLetter, note.getColor());
        holder.noteDrawable.setImageDrawable(noteDrawable);
        holder.title.setText(note.getTitle());
        holder.updatedTime.setText(DATE_FORMAT.format(note.getUpdatedAt()));
    }

    private void onNoteSelected(int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onNoteSelected(position);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        final ImageView noteDrawable;
        final TextView title;
        final TextView updatedTime;

        NoteViewHolder(View itemView) {
            super(itemView);

            this.noteDrawable = itemView.findViewById(R.id.note_drawable);
            this.title = itemView.findViewById(R.id.note_title);
            this.updatedTime = itemView.findViewById(R.id.note_updated_time);
        }


    }

}
