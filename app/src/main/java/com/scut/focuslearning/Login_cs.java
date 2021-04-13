package com.scut.focuslearning;

import com.google.gson.annotations.SerializedName;

public class Login_cs {


    private String username;
    private String adminname;
    private String code;
    @SerializedName("Status Code")
    private int _$StatusCode185; // FIXME check this code

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int get_$StatusCode185() {
        return _$StatusCode185;
    }

    public void set_$StatusCode185(int _$StatusCode185) {
        this._$StatusCode185 = _$StatusCode185;
    }
}