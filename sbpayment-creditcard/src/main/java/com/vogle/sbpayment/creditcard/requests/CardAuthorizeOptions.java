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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Card authorize options
 *
 * @author Allan Im
 **/
@Data
public class CardAuthorizeOptions {

    @NotEmpty
    @JacksonXmlProperty(localName = "token")
    private String token;

    @NotEmpty
    @JacksonXmlProperty(localName = "token_key")
    private String tokenKey;

    @NotEmpty
    @Pattern(regexp = "[01]")
    @JacksonXmlProperty(localName = "cust_manage_flg")
    private String custManageFlg;

    @Pattern(regexp = "[01]")
    @JacksonXmlProperty(localName = "cardbrand_return_flg")
    private String cardbrandReturnFlg;
}
