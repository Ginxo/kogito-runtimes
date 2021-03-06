/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.integrationtests.springboot;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ElementAtIndexTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testHeaderEmpty() {
        given().body(" { \"a list\" : [\"a\", \"b\", \"c\"], \"an index\" : 1 }")
               .contentType(ContentType.JSON)
               .when()
               .post("/ElementAtIndex")
               .then()
               .statusCode(200)
               .header("X-Kogito-decision-messages", nullValue())
               .body("'element at index'", is("a"));
    }

    @Test
    public void testHeaderPopulated() {
        given().body(" { \"a list\" : [\"a\", \"b\", \"c\"], \"an index\" : 47 }")
               .contentType(ContentType.JSON)
               .when()
               .post("/ElementAtIndex")
               .then()
               .statusCode(200)
               .header("X-Kogito-decision-messages", notNullValue()) // a warning
               .body("'element at index'", nullValue());
    }

}
