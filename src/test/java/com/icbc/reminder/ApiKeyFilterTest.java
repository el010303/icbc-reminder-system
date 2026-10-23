package com.icbc.reminder;

import com.icbc.reminder.config.ApiKeyFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApiKeyFilterTest {

    private ApiKeyFilter filter;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new ApiKeyFilter();
        ReflectionTestUtils.setField(filter, "apiKey", "test-key");
        chain = mock(FilterChain.class);
    }

    private MockHttpServletResponse call(String path, String key) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        if (key != null) {
            request.addHeader("X-API-KEY", key);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        return response;
    }

    @Test
    void validKey_passesThrough() throws Exception {
        call("/dashboard/stats", "test-key");
        verify(chain).doFilter(any(), any());
    }

    @Test
    void wrongKey_returns401_andStopsTheChain() throws Exception {
        MockHttpServletResponse response = call("/dashboard/stats", "test-kez");
        assertEquals(401, response.getStatus());
        verifyNoInteractions(chain);
    }

    @Test
    void missingKey_returns401() throws Exception {
        MockHttpServletResponse response = call("/audit/logs", null);
        assertEquals(401, response.getStatus());
        verifyNoInteractions(chain);
    }

    @Test
    void swaggerUi_isPublic() throws Exception {
        call("/swagger-ui/index.html", null);
        verify(chain).doFilter(any(), any());
    }
}
