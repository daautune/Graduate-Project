package com.dtu.capstone2.ereading.network.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "item", strict = false)
public class RssItemResponse {

    @Element(name = "title")
    private String title;

    @Element(name = "link")
    private String link;

    @Element(name = "description", required = false)
    private String description;

    @Element(name = "pubDate", required = false)
    private String pushDatel;

    @ElementList(name = "content", required = false, inline = true)
    @Path("group")
    private List<RssItemMediaGroup> rssItemMediaGroups;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPushDatel() {
        return pushDatel;
    }

    public void setPushDatel(String pushDatel) {
        this.pushDatel = pushDatel;
    }

    public List<RssItemMediaGroup> getRssItemMediaGroups() {
        return rssItemMediaGroups;
    }

    public void setRssItemMediaGroups(List<RssItemMediaGroup> rssItemMediaGroups) {
        this.rssItemMediaGroups = rssItemMediaGroups;
    }
}
