package com.dtu.capstone2.ereading.network.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rss", strict = false)
public class RssResponse {

    @Element(name = "title")
    @Path("channel")
    private String channelTitle;

    @ElementList(name = "item", inline = true)
    @Path("channel")
    private List<RssItemResponse> rssItemResponses;

    /**
     * @return the channelTitle
     */
    public String getChannelTitle() {
        return channelTitle;
    }

    /**
     * @param channelTitle the channelTitle to set
     */
    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    /**
     * @return the rssItemResponses
     */
    public List<RssItemResponse> getRssItemResponses() {
        return rssItemResponses;
    }

    /**
     * @param rssItemResponses the rssItemResponses to set
     */
    public void setRssItemResponses(List<RssItemResponse> rssItemResponses) {
        this.rssItemResponses = rssItemResponses;
    }

}
