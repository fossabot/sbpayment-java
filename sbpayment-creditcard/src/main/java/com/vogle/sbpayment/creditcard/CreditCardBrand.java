package com.vogle.sbpayment.creditcard;

/**
 * Card Brand Type
 *
 * @author Allan Im
 **/
public enum CreditCardBrand {

    JCB("J"), VISA("V"), MASTER("M"), AMEX("A"), DINERS("D"), OTHER("X");

    private String spsCardbrandCode;

    CreditCardBrand(String spsCardbrandCode) {
        this.spsCardbrandCode = spsCardbrandCode;
    }

    public static CreditCardBrand brand(String code) {
        for (CreditCardBrand brand : CreditCardBrand.values()) {
            if (code.equalsIgnoreCase(brand.getSpsCardbrandCode())) {
                return brand;
            }
        }
        return OTHER;
    }

    public String getSpsCardbrandCode() {
        return spsCardbrandCode;
    }
}