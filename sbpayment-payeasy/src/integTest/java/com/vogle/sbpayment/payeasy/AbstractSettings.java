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

package com.vogle.sbpayment.payeasy;

import com.vogle.sbpayment.client.DefaultSbpayment;
import com.vogle.sbpayment.client.Sbpayment;
import com.vogle.sbpayment.client.SpsConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * Test config
 *
 * @author Allan Im
 **/
abstract class AbstractSettings {
    private SpsConfig config;

    AbstractSettings() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(System.getProperty("user.dir") + File.separator
                    + "../config/it2.properties"));
        } catch (IOException ignored) {
        }

        config = SpsConfig.from(p);
    }

    SpsConfig getConfig() {
        return config;
    }

    Sbpayment sbpayment() {
        return new DefaultSbpayment(config);
    }

    String orderNo() {
        Random random = new Random();
        return "VO" + dayPattern() + random.nextInt(99_999);
    }

    private String dayPattern() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        long time = System.currentTimeMillis();
        return fmt.format(new Date(time));
    }
}
