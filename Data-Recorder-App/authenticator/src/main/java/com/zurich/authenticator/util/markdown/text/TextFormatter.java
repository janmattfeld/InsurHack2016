package com.zurich.authenticator.util.markdown.text;

import com.zurich.authenticator.util.markdown.MarkdownElement;

public abstract class TextFormatter extends MarkdownElement {

    public final String predecessor = getPredecessor();
    public final String successor = getSuccessor();

    Object value;

    public TextFormatter(Object value) {
        this.value = value;
    }

    @Override
    public String serialize() {
        return predecessor + value.toString() + successor;
    }

    public String getPredecessor() {
        return "";
    }

    public String getSuccessor() {
        return "";
    }
}
