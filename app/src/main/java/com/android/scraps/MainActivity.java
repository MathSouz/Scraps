package com.android.scraps;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.scraps.database.realm.ScrapCollection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends FragmentActivity
{
    private ScrapApp scrapApp;
    private ViewPager2 viewPager;
    private FloatingActionButton floatingActionButton;
    protected int lastPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final Realm realm = Realm.getDefaultInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.scrapApp = (ScrapApp)getApplication();
        this.viewPager = findViewById(R.id.view_pager);
        this.floatingActionButton = findViewById(R.id.floatingActionButton);
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                ScrapCollection scrapCollection = new ScrapCollection();

                if(realm.where(ScrapCollection.class).findAll().size() > 0)
                {
                    Number number = realm.where(ScrapCollection.class).max("id");
                    scrapCollection.setId(number.longValue() + 1);
                }

                scrapCollection.setTitle("Scrap Collection");
                scrapCollection.setDescription("This is my scrap collection!");
                realm.copyToRealm(scrapCollection);
                realm.commitTransaction();
            }
        });

        RealmResults<ScrapCollection> collections = realm.where(ScrapCollection.class).findAll();
        updateCollectionListView(collections);

        collections.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<ScrapCollection>>() {
            @Override
            public void onChange(@NotNull RealmResults<ScrapCollection> scrapCollections, @NotNull OrderedCollectionChangeSet changeSet)
            {
                updateCollectionListView(scrapCollections);
                viewPager.setCurrentItem(lastPageIndex, false);
            }
        });
    }

    public class ViewPagerAdapter extends FragmentStateAdapter
    {
        private List<ScrapCollection> scrapBooks;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<ScrapCollection> scrapBooks)
        {
            super(fragmentActivity);
            this.scrapBooks = scrapBooks;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position)
        {
            ScrapCollection scrapBook = scrapBooks.get(position);
            return new ScrapCoverFragment(MainActivity.this, scrapBook, position);
        }

        @Override
        public int getItemCount()
        {
            return scrapBooks.size();
        }
    }

    public void updateCollectionListView(List<ScrapCollection> scrapCollections)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(MainActivity.this, scrapCollections);
        viewPager.setAdapter(adapter);
    }

    public void updateCollectionListView()
    {
        updateCollectionListView(Realm.getDefaultInstance().where(ScrapCollection.class).findAll());
    }
}