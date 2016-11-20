package com.zurich.authenticator.data.state;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.state.manager.StateManager;
import com.zurich.authenticator.util.TimeUtils;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.table.TableRow;

import java.util.ArrayList;
import java.util.List;

public class StateData extends Data {

    int stateType = Data.TYPE_NOT_SET;
    @Expose
    float value;

    public StateData() {
        super(Data.TYPE_STATE);
    }

    public StateData(int stateType) {
        this();
        this.stateType = stateType;
    }

    public StateData(int stateType, float value) {
        this();
        this.stateType = stateType;
        this.value = value;
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString().replace("|", "∙");
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        List<Object> columns = new ArrayList<>();
        columns.add(String.format(java.util.Locale.US, "%.4f", value));
        columns.add(StateManager.getReadableStateType(stateType));
        columns.add(TimeUtils.getReadableTimeSince(timestamp));
        return new TableRow(columns);
    }

    public int getStateType() {
        return stateType;
    }

    public void setStateType(int stateType) {
        this.stateType = stateType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
