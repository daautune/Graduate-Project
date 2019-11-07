package com.dtu.capstone2.ereading.ui.newfeed.listnewfeed;

import com.dtu.capstone2.ereading.ui.model.ItemListNewFeedPager;
import com.dtu.capstone2.ereading.ui.newfeed.listnewfeed.pagelistnewfeed.PageListNewFeedFragment;

import java.util.ArrayList;
import java.util.List;

class ListNewFeedViewModel {
    private List<ItemListNewFeedPager> mListNewFeedBBCPopularPagers = new ArrayList<>();
    private List<ItemListNewFeedPager> mListNewFeedBBCGlobalAndUKPagers = new ArrayList<>();
    private List<ItemListNewFeedPager> mListNewFeedBBCSportPagers = new ArrayList<>();
    private List<List<ItemListNewFeedPager>> groupNewFeed = new ArrayList<>();
    private Integer positionGroup = 0;

    private List<ItemListNewFeedPager> getListItemPagersFromSourceBBCPopular() {
        mListNewFeedBBCPopularPagers.clear();
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Top Stories", "news/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "World", "news/world/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "UK", "news/uk/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Business", "news/business/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Politics", "news/politics/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Health", "news/health/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Education & Family", "news/education/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Science & Environment", "news/science_and_environment/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Technology", "news/technology/rss.xml"));
        mListNewFeedBBCPopularPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Entertainment & Arts", "news/entertainment_and_arts/rss.xml"));
        return mListNewFeedBBCPopularPagers;
    }

    private List<ItemListNewFeedPager> getListItemPagersFromSourceBBCGlobalAndUK() {
        mListNewFeedBBCGlobalAndUKPagers.clear();
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Africa", "news/world/africa/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Asia", "news/world/asia/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Europe", "news/world/europe/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Latin America", "news/world/latin_america/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Middle East", "news/world/middle_east/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "US & Canada", "news/world/us_and_canada/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "England", "news/england/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Northern Ireland", "news/northern_ireland/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Scotland", "news/scotland/rss.xml"));
        mListNewFeedBBCGlobalAndUKPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Wales", "news/wales/rss.xml"));
        return mListNewFeedBBCGlobalAndUKPagers;
    }

    private List<ItemListNewFeedPager> getListItemPagersFromSourceBBCSports() {
        mListNewFeedBBCSportPagers.clear();
        mListNewFeedBBCSportPagers.add(new ItemListNewFeedPager(new PageListNewFeedFragment(), "Top Sports", "sport/rss.xml"));
        return mListNewFeedBBCSportPagers;
    }

    List<List<ItemListNewFeedPager>> getGroupNewFeed() {
        groupNewFeed.clear();
        groupNewFeed.add(getListItemPagersFromSourceBBCPopular());
        groupNewFeed.add(getListItemPagersFromSourceBBCGlobalAndUK());
        groupNewFeed.add(getListItemPagersFromSourceBBCSports());
        return groupNewFeed;
    }

    Integer getPositionGroup() {
        return positionGroup;
    }

    void setPositionGroup(Integer positionGroup) {
        this.positionGroup = positionGroup;
    }
}
