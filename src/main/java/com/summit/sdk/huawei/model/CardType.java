package com.summit.sdk.huawei.model;

public enum CardType {

    IDENTITY(0,"身份证"),
    PASSPORT(1,"护照"),
    OFFICER(2,"军官证"),
    DRIVING(3,"驾驶证"),
    OTHERS(4,"其他");

    private int cardTypeCode;
    private String cardTypeDescription;

     CardType(int cardTypeCode, String cardTypeDescription) {
        this.cardTypeCode = cardTypeCode;
        this.cardTypeDescription = cardTypeDescription;
    }


    public static CardType codeOf(int cardTypeCode) {
        for (CardType v : values()) {
            if (v.cardTypeCode == cardTypeCode) {
                return v;
            }
        }
        return null;
    }

    public int getCardTypeCode() {
        return cardTypeCode;
    }

    public String getCardTypeDescription() {
        return cardTypeDescription;
    }
}
