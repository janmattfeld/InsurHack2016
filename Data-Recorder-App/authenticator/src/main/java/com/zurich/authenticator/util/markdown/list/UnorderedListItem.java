package com.zurich.authenticator.util.markdown.list;

import com.zurich.authenticator.util.markdown.text.TextFormatter;

public class UnorderedListItem extends TextFormatter {

    public UnorderedListItem(Object value) {
        super(value);
    }

    @Override
    public String getPredecessor() {
        return "- ";
    }

    @Override
    public String getSuccessor() {
        return "";
    }

}
