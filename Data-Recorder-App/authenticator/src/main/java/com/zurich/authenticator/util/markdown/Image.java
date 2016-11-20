package com.zurich.authenticator.util.markdown;

public class Image extends Link {

    public Image(Object text, String url) {
        super(text, url);
    }

    public Image(String url) {
        this(url, url);
    }

    @Override
    public String serialize() {
        return "!" + super.serialize();
    }

}
