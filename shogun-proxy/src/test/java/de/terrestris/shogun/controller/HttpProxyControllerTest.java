/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2021-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.controller;

import de.terrestris.shogun.service.HttpProxyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of {@link HttpProxyController}
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
public class HttpProxyControllerTest {

    private MockMvc mockMvc;

    @MockBean
    protected HttpProxyService proxyService;

    @MockBean
    private HttpProxyController httpProxyController;

    private final String PROXY_ENDPOINT = "/proxy.action";

    @BeforeEach
    public void setup() {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(httpProxyController).build();
    }

    @Test
    public void proxyShouldWorkAsExpected() throws Exception {
        String baseUrl = "https://terrestris.de";
        final String body = "TEST";
        ResponseEntity mockedResponse = new ResponseEntity(body, null, HttpStatus.OK);

        Mockito.when(proxyService.doProxy(
            Matchers.any(HttpServletRequest.class),
            Matchers.any(String.class),
            Matchers.any(Map.class))
        ).thenReturn(mockedResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(PROXY_ENDPOINT)
                .param("baseUrl", baseUrl))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("Returned body matched mocked one.", body, content);

        Mockito.verify(proxyService, Mockito.times(1)).
            doProxy(Matchers.any(HttpServletRequest.class),
                Matchers.any(String.class),
                Matchers.any(Map.class));
        Mockito.verifyNoMoreInteractions(proxyService);
    }
}
