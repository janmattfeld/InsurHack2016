package com.zurich.authenticator.util.markdown.text;

public class CodeText extends TextFormatter {

    public CodeText(Object value) {
        super(value);
    }

    @Override
    public String getPredecessor() {
        return "`";
    }

    @Override
    public String getSuccessor() {
        return getPredecessor();
    }

}
