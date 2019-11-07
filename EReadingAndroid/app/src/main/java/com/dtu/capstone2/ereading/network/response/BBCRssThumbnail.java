package com.dtu.capstone2.ereading.network.response;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "thumbnail")
public class BBCRssThumbnail {
    @Attribute(name = "url", required = false)
    private String urlImage;

    @Attribute(name = "height", required = false)
    private String heightImage;

    @Attribute(name = "width", required = false)
    private String widthImage;

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getHeightImage() {
        return heightImage;
    }

    public void setHeightImage(String heightImage) {
        this.heightImage = heightImage;
    }

    public String getWidthImage() {
        return widthImage;
    }

    public void setWidthImage(String widthImage) {
        this.widthImage = widthImage;
    }
}
