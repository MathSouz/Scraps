package com.android.scraps;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.FileUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.scraps.database.realm.ScrapCollection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

public class ScrapCoverFragment extends Fragment implements MenuItem.OnMenuItemClickListener {
    private final MainActivity mainActivity;
    private ScrapCollection scrapBook;
    private Toolbar toolbar;
    private EditText titleView;
    private ImageView coverView;
    private View cardView;
    private final int pos;

    public ScrapCoverFragment(MainActivity mainActivity, ScrapCollection scrapBook, int position)
    {
        this.mainActivity = mainActivity;
        this.scrapBook = scrapBook;
        this.pos = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_scrap_cover, container, false);
        this.cardView = view.findViewById(R.id.card_view);
        this.titleView = view.findViewById(R.id.edit_title_view);
        this.toolbar = view.findViewById(R.id.card_toolbar);
        this.coverView = view.findViewById(R.id.cover_view);

        this.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mainActivity, ChapterListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("collectionId", scrapBook.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        for (int i = 0; i < toolbar.getMenu().size(); i++)
        {
            this.toolbar.getMenu().getItem(i).setOnMenuItemClickListener(this);
        }
        this.titleView.setText(scrapBook.getTitle());
        this.titleView.setImeActionLabel("done", EditorInfo.IME_ACTION_DONE);
        this.titleView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ScrapCollection collection = realm.where(ScrapCollection.class).equalTo("id", scrapBook.getId()).findFirst();

                            if(collection != null)
                            {
                                collection.setTitle(titleView.getText().toString());
                            }
                        }
                    });
                    titleView.clearFocus();
                    return true;
                }
                
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mainActivity.lastPageIndex = pos;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(item.getItemId() == R.id.delete)
        {
            Realm realm = Realm.getDefaultInstance();
            ScrapCollection collection = realm.where(ScrapCollection.class).equalTo("id", scrapBook.getId()).findFirst();

            if(collection != null)
            {
                realm.beginTransaction();
                collection.deleteFromRealm();
                mainActivity.updateCollectionListView();
                realm.commitTransaction();
            }
        }

        if(item.getItemId() == R.id.change_cover)
        {
            File file = new File(Environment.getDataDirectory(), "/text.sc");

            if(file.exists())
            {
                file.delete();
                System.out.println("File deleted");
            }

            else
            {
                try {
                    file.createNewFile();
                    System.out.println("File created");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}