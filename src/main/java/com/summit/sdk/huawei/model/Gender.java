package com.summit.sdk.huawei.model;

public enum Gender {
    

    PU_MALE(0, "男"),
    PU_FEMALE(1, "女"),
    PU_GENDER_UNKNOW(2, "未知");


    private int genderCode;
    private String genderDescription;


    Gender(int genderCode, String genderDescription) {
        this.genderCode = genderCode;
        this.genderDescription = genderDescription;
    }

    public static Gender codeOf(int genderCode) {
        for (Gender v : values()) {
            if (v.genderCode == genderCode) {
                return v;
            }
        }
        return null;
    }

    public int getGenderCode() {
        return genderCode;
    }

    public String getGenderDescription() {
        return genderDescription;
    }
}
