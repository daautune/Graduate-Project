package com.dtu.capstone2.ereading.network.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rss", strict = false)
public class BBCRssResponse {

    @Element(name = "title")
    @Path("channel")
    private String channelTitle;

    @Element(name = "description")
    @Path("channel")
    private String description;

    @ElementList(name = "item", inline = true)
    @Path("channel")
    private List<BBCRssItemResponse> bbcRssItemResponses;

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<BBCRssItemResponse> getBbcRssItemResponses() {
        return bbcRssItemResponses;
    }

    public void setBbcRssItemResponses(List<BBCRssItemResponse> bbcRssItemResponses) {
        this.bbcRssItemResponses = bbcRssItemResponses;
    }
}
