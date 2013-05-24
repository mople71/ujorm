/*
 * Copyright 2013 ponec.
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
package org.ujorm.hotels.config;

import org.ujorm.hotels.domains.Customer;
import org.ujorm.hotels.domains.Hotel;
import org.ujorm.orm.InitializationBatch;
import org.ujorm.orm.Session;

/**
 * Data loader from CSV files
 * @author ponec
 */
public class DataLoader implements InitializationBatch {

    /** Load data from a CSV file */
    @Override
    public void run(Session session) throws Exception {
        if (session.exists(Customer.class)) {
            System.out.println("TODO: load customers");
        }
        if (session.exists(Hotel.class)) {
            System.out.println("TODO: load hotels");
        }
    }
}
