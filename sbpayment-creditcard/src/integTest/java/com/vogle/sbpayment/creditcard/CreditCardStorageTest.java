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

package com.vogle.sbpayment.creditcard;

import com.vogle.sbpayment.client.SpsResult;
import com.vogle.sbpayment.client.params.PaymentInfo;
import com.vogle.sbpayment.creditcard.params.ByCreditCard;
import com.vogle.sbpayment.creditcard.params.BySavedCard;
import com.vogle.sbpayment.creditcard.params.CardInfoResponseType;
import com.vogle.sbpayment.creditcard.params.SaveCreditCard;
import com.vogle.sbpayment.creditcard.responses.CardAuthorizeResponse;
import com.vogle.sbpayment.creditcard.responses.CardInfoDeleteResponse;
import com.vogle.sbpayment.creditcard.responses.CardInfoLookupMethodInfo;
import com.vogle.sbpayment.creditcard.responses.CardInfoLookupResponse;
import com.vogle.sbpayment.creditcard.responses.LegacyCardInfoSaveResponse;
import com.vogle.sbpayment.creditcard.responses.LegacyCardInfoUpdateResponse;

import org.junit.Before;
import org.junit.Test;

import static com.vogle.sbpayment.creditcard.CardPayFeature.RETURN_CARD_BRAND;
import static com.vogle.sbpayment.creditcard.CardPayFeature.RETURN_CUSTOMER_INFO;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CreditCardPayment} to save credit information
 *
 * @author Allan Im
 **/
public class CreditCardStorageTest extends AbstractSettings {

    private CreditCardPayment payment;

    @Before
    public void init() {
        payment = CreditCardPayment.newInstance(sbpayment, RETURN_CARD_BRAND, RETURN_CUSTOMER_INFO);
    }

    @Test
    public void storage() {
        // given
        String customerCode = customerCode();
        SaveCreditCard.Builder creditCard = SaveCreditCard.builder()
                .number("4123450131003312")
                .expiration("202412")
                .securityCode("123")
                .resrv1("テスト１")
                .resrv2("テスト２")
                .resrv3("テスト３");

        // when save
        SpsResult<LegacyCardInfoSaveResponse> save = payment.saveCard(customerCode, creditCard.build());
        assertCommon(save);
        assertThat(save.getBody().isSuccess()).isTrue();
        assertThat(save.getBody().getSpsTransactionId()).isNotBlank();

        assertThat(save.getBody().getPayMethodInfo().getCreditCardBrand()).isEqualTo(CreditCardBrand.VISA);
        assertThat(save.getBody().getSpsInfo()).isNotNull();

        // when save 2nd fail
        SpsResult<LegacyCardInfoSaveResponse> save2 = payment.saveCard(customerCode, creditCard.build());
        assertCommon(save2);
        assertThat(save2.getBody().isSuccess()).isFalse();

        // lookup normal
        SpsResult<CardInfoLookupResponse> lookup = payment.lookupCard(customerCode);
        assertCommon(lookup);
        assertThat(lookup.getBody().isSuccess()).isTrue();
        assertThat(lookup.getBody().getSpsTransactionId()).isNotBlank();

        CardInfoLookupMethodInfo cardInfoNormal = lookup.getBody().getPayMethodInfo();
        assertThat(cardInfoNormal.getCcNumber()).isNullOrEmpty();
        assertThat(cardInfoNormal.getCcExpiration()).isNullOrEmpty();
        assertThat(cardInfoNormal.getCreditCardBrand()).isEqualTo(CreditCardBrand.VISA);
        assertThat(cardInfoNormal.getResrv1()).isEqualTo("テスト１");
        assertThat(cardInfoNormal.getResrv2()).isEqualTo("テスト２");
        assertThat(cardInfoNormal.getResrv3()).isEqualTo("テスト３");

        // update
        creditCard.resrv1("更新１").resrv2("更新２").resrv3("更新３");
        SpsResult<LegacyCardInfoUpdateResponse> update = payment.updateCard(customerCode, creditCard.build());
        assertCommon(update);
        assertThat(update.getBody().getSpsTransactionId()).isNotBlank();
        assertThat(update.getBody().isSuccess()).isTrue();

        assertThat(update.getBody().getPayMethodInfo().getCreditCardBrand()).isEqualTo(CreditCardBrand.VISA);
        assertThat(update.getBody().getSpsInfo()).isNotNull();

        // lookup LOWER4
        SpsResult<CardInfoLookupResponse> lookupLower4 = payment.lookupCard(customerCode, CardInfoResponseType.LOWER4);
        assertCommon(lookupLower4);
        assertThat(lookupLower4.getBody().isSuccess()).isTrue();
        assertThat(lookupLower4.getBody().getSpsTransactionId()).isNotBlank();

        CardInfoLookupMethodInfo cardInfoLower4 = lookupLower4.getBody().getPayMethodInfo();
        assertThat(cardInfoLower4.getCcNumber().startsWith("****")).isTrue();
        assertThat(cardInfoLower4.getCcNumber().endsWith("3312")).isTrue();
        assertThat(cardInfoLower4.getCcExpiration()).isEqualTo("202412");
        assertThat(cardInfoLower4.getCreditCardBrand()).isEqualTo(CreditCardBrand.VISA);
        assertThat(cardInfoLower4.getResrv1()).isEqualTo("更新１");
        assertThat(cardInfoLower4.getResrv2()).isEqualTo("更新２");
        assertThat(cardInfoLower4.getResrv3()).isEqualTo("更新３");

        // lookup ALL_MASK
        SpsResult<CardInfoLookupResponse> lookupAllMask = payment.lookupCard(customerCode, CardInfoResponseType.ALL_MASK);
        assertCommon(lookupAllMask);
        assertThat(lookupAllMask.getBody().getSpsTransactionId()).isNotBlank();
        assertThat(lookupAllMask.getBody().isSuccess()).isTrue();

        CardInfoLookupMethodInfo cardInfoAllMask = lookupAllMask.getBody().getPayMethodInfo();
        assertThat(cardInfoAllMask.getCcNumber().startsWith("****")).isTrue();
        assertThat(cardInfoAllMask.getCcNumber().endsWith("****")).isTrue();
        assertThat(cardInfoAllMask.getCcExpiration()).isEqualTo("202412");
        assertThat(cardInfoAllMask.getResrv1()).isEqualTo("更新１");
        assertThat(cardInfoAllMask.getResrv2()).isEqualTo("更新２");
        assertThat(cardInfoAllMask.getResrv3()).isEqualTo("更新３");
        assertThat(cardInfoAllMask.getCreditCardBrand()).isEqualTo(CreditCardBrand.VISA);

        // delete
        SpsResult<CardInfoDeleteResponse> delete = payment.deleteCard(customerCode);
        assertCommon(delete);
        assertThat(delete.getBody().isSuccess()).isTrue();
        assertThat(delete.getBody().getSpsTransactionId()).isNotBlank();

        // lookup non-data fail
        SpsResult<CardInfoLookupResponse> lookupNoData = payment.lookupCard(customerCode);
        assertCommon(lookupNoData);
        assertThat(lookupNoData.getBody().isSuccess()).isFalse();
    }

    @Test
    public void authThenSave() {
        String customerCode = customerCode();

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .customerCode(customerCode)
                .orderId(orderNo())
                .itemId("ORDERITEMID")
                .itemName("日本語")
                .amount(1080)
                .tax(80)
                .build();

        ByCreditCard creditCard = ByCreditCard.builder()
                .number("4123450131003312")
                .expiration("202412")
                .securityCode("123")
                .savingCreditCard(true)
                .build();

        // authorize
        SpsResult<CardAuthorizeResponse> authorize = payment.authorize(paymentInfo, creditCard);
        assertCommon(authorize);
        assertThat(authorize.getBody().isSuccess()).isTrue();
        assertThat(authorize.getBody().getSpsTransactionId()).isNotBlank();

        // lookup
        SpsResult<CardInfoLookupResponse> lookup = payment.lookupCard(customerCode, CardInfoResponseType.LOWER4);
        assertCommon(lookup);
        assertThat(lookup.getBody().isSuccess()).isTrue();
        assertThat(lookup.getBody().getSpsTransactionId()).isNotBlank();

        CardInfoLookupMethodInfo cardInfoLower4 = lookup.getBody().getPayMethodInfo();
        assertThat(cardInfoLower4.getCcNumber().startsWith("****")).isTrue();
        assertThat(cardInfoLower4.getCcNumber().endsWith("3312")).isTrue();
        assertThat(cardInfoLower4.getCcExpiration()).isEqualTo("202412");
        assertThat(cardInfoLower4.getCreditCardBrand()).isEqualTo(CreditCardBrand.VISA);

        // delete
        SpsResult<CardInfoDeleteResponse> delete = payment.deleteCard(customerCode);
        assertCommon(delete);
        assertThat(delete.getBody().isSuccess()).isTrue();
    }

    @Test
    public void saveThenAuth() {
        String customerCode = customerCode();
        SaveCreditCard creditCard = SaveCreditCard.builder()
                .number("4123450131003312")
                .expiration("202412")
                .securityCode("123")
                .resrv1("テスト１0")
                .resrv2("テスト２0")
                .resrv3("テスト３0")
                .build();

        // save
        SpsResult<LegacyCardInfoSaveResponse> save = payment.saveCard(customerCode, creditCard);
        assertCommon(save);
        assertThat(save.getBody().isSuccess()).isTrue();
        assertThat(save.getBody().getSpsTransactionId()).isNotBlank();

        assertThat(save.getBody().getPayMethodInfo().getCreditCardBrand()).isEqualTo(CreditCardBrand.VISA);
        assertThat(save.getBody().getSpsInfo()).isNotNull();

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .customerCode(customerCode)
                .orderId(orderNo())
                .itemId("ORDERITEMID")
                .itemName("日本語")
                .amount(1080)
                .tax(80)
                .build();

        BySavedCard savedCard = BySavedCard.builder()
                .dealingsType(DealingsType.LUMP_SUM)
                .build();

        // authorize
        SpsResult<CardAuthorizeResponse> authorize = payment.authorize(paymentInfo, savedCard);
        assertCommon(authorize);
        assertThat(authorize.getBody().isSuccess()).isTrue();
        assertThat(authorize.getBody().getSpsTransactionId()).isNotBlank();

        // lookup
        SpsResult<CardInfoLookupResponse> lookup = payment.lookupCard(customerCode, CardInfoResponseType.LOWER4);
        assertCommon(lookup);
        assertThat(lookup.getBody().isSuccess()).isTrue();
        assertThat(lookup.getBody().getSpsTransactionId()).isNotBlank();

        CardInfoLookupMethodInfo cardInfoLower4 = lookup.getBody().getPayMethodInfo();
        assertThat(cardInfoLower4.getCcNumber().startsWith("****")).isTrue();
        assertThat(cardInfoLower4.getCcNumber().endsWith("3312")).isTrue();
        assertThat(cardInfoLower4.getCcExpiration()).isEqualTo("202412");
        assertThat(cardInfoLower4.getCreditCardBrand()).isEqualTo(CreditCardBrand.VISA);
        assertThat(cardInfoLower4.getResrv1()).isEqualTo("テスト１0");
        assertThat(cardInfoLower4.getResrv2()).isEqualTo("テスト２0");
        assertThat(cardInfoLower4.getResrv3()).isEqualTo("テスト３0");

        // delete
        SpsResult<CardInfoDeleteResponse> delete = payment.deleteCard(customerCode);
        assertCommon(delete);
        assertThat(delete.getBody().isSuccess()).isTrue();
    }

    @Test
    public void delete() {
        String customerCode = "TEST_CUSTOMER_CODE";
        SpsResult<CardInfoDeleteResponse> delete = payment.deleteCard(customerCode);
        assertCommon(delete);
        assertThat(delete.getBody().isSuccess()).isFalse();
    }

    private void assertCommon(SpsResult<?> result) {
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getBody()).isNotNull();
    }

}
