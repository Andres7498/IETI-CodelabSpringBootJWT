package org.adaschool.api.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;

import static org.adaschool.api.utils.Constants.MISSING_TOKEN_ERROR_MESSAGE;
import static org.adaschool.api.utils.Constants.TOKEN_EXPIRED_MALFORMED_ERROR_MESSAGE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {"spring.data.mongodb.uri=mongodb://localhost/testdb", "jwt.secret=secret"})
public class JwtRequestFilterTest {

    private final JwtRequestFilter jwtRequestFilter = new JwtRequestFilter("secret");
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Test
    public void whenOptionsMethodRequestThenRespondsOkStatus() throws Exception {
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("OPTIONS");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenRequestHasNoAuthorizationHeaderThenRespondsUnauthorizedStatus() throws Exception {
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, MISSING_TOKEN_ERROR_MESSAGE);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenRequestHasNoBearerTokenThenRespondsUnauthorizedStatus() throws Exception {
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Token");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, MISSING_TOKEN_ERROR_MESSAGE);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenRequestHasInvalidTokenThenRespondsUnauthorized() throws Exception {
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2M2VlNThhNzIxYjliYzc1Y2RlNzgyYTEiLCJhZGFfcm9sZXMiOlsiVVNFUiJdLCJpYXQiOjE2NzY1NjQ2NzQsImV4cCI6MTY3NjY1MTA3NH0.I3KIIfVWLLMfSGv6AHwrYiZlEvHGC1gPmBolilKCJ1o");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, TOKEN_EXPIRED_MALFORMED_ERROR_MESSAGE);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenRequestHasValidTokenThenRespondsOK() throws Exception {
        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2M2VlNThhNzIxYjliYzc1Y2RlNzgyYTEiLCJhZGFfcm9sZXMiOlsiVVNFUiJdLCJpYXQiOjE2Nzc1NDc4MDksImV4cCI6MTk5MjkwNzgwOX0.l0bezClGPysTa52gOfL3yCVxevsgHMQIDcrKVYQNGUY";
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenAuthRequestTheDoFilterIsCalled() throws Exception {
        when(request.getRequestURI()).thenReturn("v1/auth");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void securityContextIsSetTest() throws Exception {
        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2M2VlNThhNzIxYjliYzc1Y2RlNzgyYTEiLCJhZGFfcm9sZXMiOlsiVVNFUiJdLCJpYXQiOjE2Nzc1NDc4MDksImV4cCI6MTk5MjkwNzgwOX0.l0bezClGPysTa52gOfL3yCVxevsgHMQIDcrKVYQNGUY";
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assertions.assertEquals(userId, "63ee58a721b9bc75cde782a1");
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String userRole = ((GrantedAuthority) ((ArrayList) authorities).get(0)).getAuthority();
        Assertions.assertEquals(userRole, "ROLE_USER");

    }

}
