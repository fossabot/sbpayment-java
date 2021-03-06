/*
 * Copyright 2019 Vogle Labs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vogle.sbpayment.creditcard.requests;

import com.vogle.sbpayment.client.convert.CipherString;
import com.vogle.sbpayment.client.convert.MultiByteString;
import com.vogle.sbpayment.client.params.PaymentInfo;
import com.vogle.sbpayment.client.requests.PayDetail;
import com.vogle.sbpayment.client.requests.SpsRequest;
import com.vogle.sbpayment.creditcard.responses.CardAuthorizeResponse;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.vogle.sbpayment.client.requests.RequestHelper.mapItem;

/**
 * Credit card authorize request<br/>
 * 決済要求（トークン利用）
 *
 * @author Allan Im
 **/
@Data
@JacksonXmlRootElement(localName = "sps-api-request")
public class CardAuthorizeRequest implements SpsRequest<CardAuthorizeResponse> {

    @JacksonXmlProperty(isAttribute = true)
    private final String id = "ST01-00131-101";

    @NotEmpty
    @Pattern(regexp = "[0-9]{5}")
    @JacksonXmlProperty(localName = "merchant_id")
    private String merchantId;

    @NotEmpty
    @Pattern(regexp = "[0-9]{3}")
    @JacksonXmlProperty(localName = "service_id")
    private String serviceId;

    @NotEmpty
    @Size(max = 64)
    @JacksonXmlProperty(localName = "cust_code")
    private String custCode;

    @NotEmpty
    @Size(max = 38)
    @JacksonXmlProperty(localName = "order_id")
    private String orderId;

    @NotEmpty
    @Size(max = 32)
    @JacksonXmlProperty(localName = "item_id")
    private String itemId;

    @Size(max = 40)
    @MultiByteString
    @JacksonXmlProperty(localName = "item_name")
    private String itemName;

    @Max(9_999_999)
    @Min(0)
    @JacksonXmlProperty(localName = "tax")
    private Integer tax;

    @NotNull
    @Max(9_999_999)
    @Min(1)
    @JacksonXmlProperty(localName = "amount")
    private Integer amount;

    @Size(max = 20)
    @MultiByteString
    @JacksonXmlProperty(localName = "free1")
    private String free1;

    @Size(max = 20)
    @MultiByteString
    @JacksonXmlProperty(localName = "free2")
    private String free2;

    @Size(max = 20)
    @MultiByteString
    @JacksonXmlProperty(localName = "free3")
    private String free3;

    @Max(99)
    @JacksonXmlProperty(localName = "order_rowno")
    private Integer orderRowno;

    @Pattern(regexp = "[01]")
    @JacksonXmlProperty(localName = "sps_cust_info_return_flg")
    private String spsCustInfoReturnFlg;

    @Valid
    @MultiByteString
    @JacksonXmlElementWrapper(localName = "dtls")
    @JacksonXmlProperty(localName = "dtl")
    private List<PayDetail> payDetails;

    @Valid
    @NotNull
    @CipherString
    @MultiByteString
    @JacksonXmlProperty(localName = "pay_method_info")
    private CardAuthorizeMethod payMethod;

    @Valid
    @NotNull
    @JacksonXmlProperty(localName = "pay_option_manage")
    private CardAuthorizeOptions payOptions;

    @Pattern(regexp = "[01]")
    @JacksonXmlProperty(localName = "encrypted_flg")
    private String encryptedFlg;

    @NotEmpty
    @JacksonXmlProperty(localName = "request_date")
    private String requestDate;

    @Max(9_999)
    @JacksonXmlProperty(localName = "limit_second")
    private Integer limitSecond;

    @Size(max = 40)
    @JacksonXmlProperty(localName = "sps_hashcode")
    private String spsHashcode;

    @Override
    public Class<CardAuthorizeResponse> responseClass() {
        return CardAuthorizeResponse.class;
    }

    /**
     * Set data from payment Info
     *
     * @param paymentInfo the payment info
     */
    public void setPaymentInfo(PaymentInfo paymentInfo) {
        // payment info
        this.setCustCode(paymentInfo.getCustomerCode());
        this.setOrderId(paymentInfo.getOrderId());
        this.setItemId(paymentInfo.getItemId());
        this.setItemName(paymentInfo.getItemName());
        this.setTax(paymentInfo.getTax());
        this.setAmount(paymentInfo.getAmount());
        this.setFree1(paymentInfo.getFree1());
        this.setFree2(paymentInfo.getFree2());
        this.setFree3(paymentInfo.getFree3());
        this.setOrderRowno(paymentInfo.getOrderRowNo());

        // item details
        this.setPayDetails(mapItem(paymentInfo.getItems()));
    }
}
