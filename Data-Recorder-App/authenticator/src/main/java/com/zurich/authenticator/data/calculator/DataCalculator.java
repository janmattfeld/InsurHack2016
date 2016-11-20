package com.zurich.authenticator.data.calculator;

import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.MarkdownSerializable;
import com.zurich.authenticator.util.markdown.text.NormalText;

public abstract class DataCalculator<Data> implements MarkdownSerializable {

    public abstract boolean canCalculate();

    public abstract Data calculate() throws DataCalculationException;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        return new NormalText(this);
    }

}
