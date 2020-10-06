package com.android.scraps;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.android.scraps.database.realm.ScrapChapter;
import com.android.scraps.database.realm.ScrapCollection;

import io.realm.Realm;

public class EditActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private EditText textArea;
    private boolean editting = true;
    private ScrapCollection collection;
    private ScrapChapter chapter;
    private TextView wordsView, charsView, linesView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        this.textArea = findViewById(R.id.text_area);
        this.toolbar = findViewById(R.id.toolbar);
        this.wordsView = findViewById(R.id.word_count_view);
        this.charsView = findViewById(R.id.chars_count_view);
        this.linesView = findViewById(R.id.line_count_view);


        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            long collectionId = bundle.getLong("collectionId");
            long chapterId = bundle.getLong("chapterId");

            final Realm realm = Realm.getDefaultInstance();
            collection = realm.where(ScrapCollection.class).equalTo("id", collectionId).findFirst();
            chapter = collection.getChapters().where().equalTo("id", chapterId).findFirst();

            update();

            this.toolbar.getMenu().findItem(R.id.edit_title_desc).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    View view = LayoutInflater.from(EditActivity.this).inflate(R.layout.dialog_chapter_titles_change, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this).setView(view);
                    final AlertDialog dialog = builder.create();
                    Button cancel = view.findViewById(R.id.cancel_button);
                    Button save = view.findViewById(R.id.save_button);
                    final EditText titleInput = view.findViewById(R.id.title_input);
                    final EditText descInput = view.findViewById(R.id.desc_input);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            realm.beginTransaction();
                            chapter.setTitle(titleInput.getText().toString());
                            chapter.setDescription(descInput.getText().toString());
                            realm.commitTransaction();
                            dialog.dismiss();
                            update();
                        }
                    });

                    titleInput.setText(chapter.getTitle());
                    descInput.setText(chapter.getDescription());

                    dialog.show();
                    return true;
                }
            });

            textArea.setEnabled(false);
            this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    editting = !editting;

                    if(editting)
                    {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        chapter.setText(textArea.getText().toString());
                        realm.commitTransaction();
                        textArea.setEnabled(false);
                        toolbar.setNavigationIcon(R.drawable.ic_baseline_edit_24);
                        toolbar.setTitle(chapter.getTitle());
                    } else {

                        textArea.setEnabled(true);
                        toolbar.setNavigationIcon(R.drawable.ic_baseline_done_24);
                    }
                }
            });
        }

        this.textArea.setText(chapter.getText());
        updateCounterViews();
        this.textArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count)
            {
                toolbar.setTitle(chapter.getTitle() + "*");
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                updateCounterViews();
            }
        });
    }

    private void updateCounterViews()
    {
        String text = textArea.getText().toString();
        int words = text.split("\\s+").length;
        int lines = text.split("\n").length;
        int chars = text.length();
        wordsView.setText(String.valueOf(words + " words"));
        charsView.setText(String.valueOf(chars + " characters"));
        linesView.setText(String.valueOf(lines + " lines"));

    }

    private void update()
    {
        this.toolbar.setTitle(chapter.getTitle());
        this.toolbar.setSubtitle(chapter.getDescription());
    }
}