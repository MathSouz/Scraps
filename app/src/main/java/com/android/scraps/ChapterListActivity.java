package com.android.scraps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.scraps.R;
import com.android.scraps.database.realm.ScrapChapter;
import com.android.scraps.database.realm.ScrapCollection;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmList;

public class ChapterListActivity extends AppCompatActivity
{
    private RecyclerView chapterList;
    private ExtendedFloatingActionButton addNewChapterButton;
    private ScrapCollection collection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);
        this.chapterList = findViewById(R.id.chapter_list);
        this.addNewChapterButton = findViewById(R.id.add_new_chapter_button);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            Realm realm = Realm.getDefaultInstance();
            long collectionId = bundle.getLong("collectionId");
            collection = realm.where(ScrapCollection.class).equalTo("id", collectionId).findFirst();
        }

        if(collection == null)
        {
            finish();
            return;
        }

        addNewChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                RealmList<ScrapChapter> chapters = collection.getChapters();

                if(chapters != null)
                {
                    ScrapChapter scrapChapter = new ScrapChapter();

                    if(chapters.size() > 0)
                    {
                        Number number = chapters.where().max("id");
                        scrapChapter.setId(number.longValue() + 1);
                    }

                    scrapChapter.setTitle("Chapter " + (chapters.size() + 1));
                    scrapChapter.setDescription("New chapter description");
                    chapters.add(scrapChapter);
                }

                realm.commitTransaction();
            }
        });


        ScrapChapter[] chapterArray = new ScrapChapter[collection.getChapters().size()];
        collection.getChapters().toArray(chapterArray);
        updateList(chapterArray);

        collection.getChapters().addChangeListener(new OrderedRealmCollectionChangeListener<RealmList<ScrapChapter>>() {
            @Override
            public void onChange(RealmList<ScrapChapter> scrapChapters, OrderedCollectionChangeSet changeSet)
            {
                ScrapChapter[] chapterArray = new ScrapChapter[scrapChapters.size()];
                scrapChapters.toArray(chapterArray);
                updateList(chapterArray);
            }
        });
    }

    private void updateList(ScrapChapter[] scrapChapters)
    {
        this.chapterList.setLayoutManager(new LinearLayoutManager(this));
        this.chapterList.setAdapter(new Adapter(scrapChapters));
    }

    private class Adapter extends RecyclerView.Adapter<Holder>
    {
        private ScrapChapter[] chapters;

        public Adapter(ScrapChapter[] chapters)
        {
            this.chapters = chapters;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(ChapterListActivity.this).inflate(R.layout.chapter_list_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position)
        {
            final ScrapChapter scrapChapter = chapters[position];
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListItemClick(scrapChapter);
                }
            });
            holder.title.setText(scrapChapter.getTitle());

            if(scrapChapter.getDescription() != null && scrapChapter.getDescription().trim().length() > 0)
            {
                holder.desc.setText(scrapChapter.getDescription());
            }

            holder.delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    new AlertDialog.Builder(ChapterListActivity.this)
                            .setMessage("Are you sure want to delete '" + scrapChapter.getTitle() + "'?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    collection.getChapters().remove(scrapChapter);
                                    realm.commitTransaction();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return chapters.length;
        }
    }

    private void onListItemClick(ScrapChapter scrapChapter)
    {
        Intent intent = new Intent(this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("collectionId", collection.getId());
        bundle.putLong("chapterId", scrapChapter.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class Holder extends RecyclerView.ViewHolder
    {
        public TextView title, desc;
        public ImageButton delete;

        public Holder(@NonNull View itemView)
        {
            super(itemView);
            this.title = itemView.findViewById(R.id.titleView);
            this.desc = itemView.findViewById(R.id.subtitleView);
            this.delete = itemView.findViewById(R.id.deleteButton);
        }
    }
}
