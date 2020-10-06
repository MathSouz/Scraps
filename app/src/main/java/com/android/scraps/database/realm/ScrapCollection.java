package com.android.scraps.database.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ScrapCollection extends RealmObject
{
    @PrimaryKey
    private long id;
    @Required
    private String title;
    private String description;
    private RealmList<ScrapChapter> chapters;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<ScrapChapter> getChapters() {
        return chapters;
    }

    public void setChapters(RealmList<ScrapChapter> chapters) {
        this.chapters = chapters;
    }
}
