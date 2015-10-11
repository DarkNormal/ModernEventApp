package com.lordan.mark.PosseUp;

/**
 * Created by Mark on 22/08/2015.
 */
public class Item {
    public String Id;
    public String Text;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    public boolean isComplete() {
        return mComplete;
    }
    public void setComplete(boolean complete) {
        mComplete = complete;
    }
}

