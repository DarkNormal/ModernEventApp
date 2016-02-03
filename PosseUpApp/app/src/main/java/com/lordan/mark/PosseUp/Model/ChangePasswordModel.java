package com.lordan.mark.PosseUp.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mark on 03/02/2016.
 */
public class ChangePasswordModel {

    @SerializedName("OldPassword")
    private String oldPassword;
    @SerializedName("NewPassword")
    private String newPassword;
    @SerializedName("ConfirmPassword")
    private String confirmPassword;

    public ChangePasswordModel(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
