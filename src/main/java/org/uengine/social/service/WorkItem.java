package org.uengine.social.service;

import org.uengine.kernel.Activity;
import org.uengine.social.entity.WorklistEntity;

/**
 * Created by uengine on 2017. 8. 22..
 */
public class WorkItem {

    WorklistEntity worklist;
    Activity activity;

    public String getDesiredState() {
        return desiredState;
    }

    public void setDesiredState(String desiredState) {
        this.desiredState = desiredState;
    }

    String desiredState;


    public WorklistEntity getWorklist() {
        return worklist;
    }

    public void setWorklist(WorklistEntity worklist) {
        this.worklist = worklist;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}
