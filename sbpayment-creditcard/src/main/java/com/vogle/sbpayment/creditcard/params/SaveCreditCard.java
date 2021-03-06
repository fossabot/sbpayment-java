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

package com.vogle.sbpayment.creditcard.params;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Credit card information
 *
 * @author Allan Im
 **/
@Getter
@ToString
@EqualsAndHashCode
@Builder(builderClassName = "Builder")
public class SaveCreditCard {

    /**
     * クレジットカード番号
     */
    @NotEmpty
    @Pattern(regexp = "[0-9]{12,19}")
    private String number;

    /**
     * クレジットカード有効期限
     */
    @NotEmpty
    @Size(min = 6, max = 6)
    @Pattern(regexp = "([12][0-9]{3})(0[1-9]|1[0-2])")
    private String expiration;

    /**
     * セキュリティコード
     */
    @Size(min = 3, max = 4)
    @Pattern(regexp = "[0-9]{3,4}")
    private String securityCode;

    @Size(max = 20)
    private String resrv1;

    @Size(max = 20)
    private String resrv2;

    @Size(max = 20)
    private String resrv3;
}
