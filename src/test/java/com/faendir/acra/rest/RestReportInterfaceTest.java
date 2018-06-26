/*
 * (C) Copyright 2018 Lukas Morawietz (https://github.com/F43nd1r)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.faendir.acra.rest;

import com.faendir.acra.model.App;
import com.faendir.acra.service.DataService;
import com.faendir.acra.service.UserService;
import junit.framework.AssertionFailedError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static com.faendir.acra.rest.RestReportInterface.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author lukas
 * @since 26.06.18
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RestReportInterface.class, includeFilters = @ComponentScan.Filter(classes = EnableWebSecurity.class))
@WithMockUser(roles = {"REPORTER", "USER"})
public class RestReportInterfaceTest {
    @MockBean UserService userService;
    @MockBean DataService dataService;
    @Autowired RestReportInterface restReportInterface;
    @Autowired MockMvc mvc;
    private final String TEST_STRING = "TEST";

    @Before
    public void setUp() throws Exception {
        App app = mock(App.class);
        when(dataService.findApp(TEST_STRING)).thenReturn(Optional.of(app));
        when(dataService.getFromReports(any(), any(), any())).thenReturn(Arrays.asList("{\"name\":\"a\"}", "{\"name\":\"b\"}"));
    }

    @Test
    public void report() throws Exception {
        mvc.perform(post("/" + REPORT_PATH).contentType(APPLICATION_JSON).content(TEST_STRING)).andExpect(status().isOk());
        verify(dataService, times(1)).createNewReport(any(), any(), any());
    }

    @Test
    public void report2() throws Exception {
        MockMultipartFile file = spy(new MockMultipartFile(TEST_STRING, TEST_STRING.getBytes()));
        when(file.getName()).thenReturn(null).thenReturn("");
        mvc.perform(multipart("/" + REPORT_PATH).file(file).contentType(MULTIPART_MIXED)).andExpect(status().isOk());
        verify(dataService, times(1)).createNewReport(any(), any(), any());
    }

    @Test
    public void exportWithId() throws Exception {
        mvc.perform(get("/" + EXPORT_PATH).param(PARAM_APP, TEST_STRING).param(PARAM_ID, TEST_STRING)).andExpect(status().isOk()).andExpect(m -> {
            JSONArray array = new JSONArray(m.getResponse().getContentAsString());
            if (array.length() != 2 || !(array.get(0) instanceof JSONObject)) {
                throw new AssertionFailedError();
            }
        });
    }

    @Test
    public void exportWithMail() throws Exception {
        mvc.perform(get("/" + EXPORT_PATH).param(PARAM_APP, TEST_STRING).param(PARAM_MAIL, TEST_STRING)).andExpect(status().isOk()).andExpect(m -> {
            JSONArray array = new JSONArray(m.getResponse().getContentAsString());
            if (array.length() != 2 || !(array.get(0) instanceof JSONObject)) {
                throw new AssertionFailedError();
            }
        });
    }

    @Test
    public void exportInvalid() throws Exception {
        mvc.perform(get("/" + EXPORT_PATH).param(PARAM_APP, TEST_STRING)).andExpect(status().is4xxClientError());
        mvc.perform(get("/" + EXPORT_PATH).param(PARAM_ID, TEST_STRING)).andExpect(status().is4xxClientError());
        mvc.perform(get("/" + EXPORT_PATH).param(PARAM_MAIL, TEST_STRING)).andExpect(status().is4xxClientError());
        mvc.perform(get("/" + EXPORT_PATH)).andExpect(status().is4xxClientError());
    }
}