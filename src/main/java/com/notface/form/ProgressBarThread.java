package com.notface.form;

import javax.swing.*;
import java.util.List;

public class ProgressBarThread extends SwingWorker<Integer,Integer> {
    Integer totalNumberOfRows;
    Integer currentRowNumber;
    JProgressBar jProgressBar;

    public ProgressBarThread(Integer totalNumberOfRows, Integer currentRowNumber, JProgressBar jProgressBar) {
        this.totalNumberOfRows = totalNumberOfRows;
        this.currentRowNumber = currentRowNumber;
        this.jProgressBar = jProgressBar;
    }

    @Override
    protected void process(List<Integer> chunks) {
        jProgressBar.setValue(chunks.get(chunks.size()-1));
    }

    @Override
    protected void done() {
        super.done();
    }

    @Override
    protected Integer doInBackground() throws Exception {
        publish(currentRowNumber);
        return null;
    }
}
