package com.scoding.metro.handler;

import com.scoding.metro.common.R;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Skip if the response is already wrapped in R
        return !returnType.getParameterType().equals(R.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        
        // Skip wrapping for certain paths
        String path = request.getURI().getPath();
        if (shouldSkip(path)) {
            return body;
        }

        // Handle String type specially
        if (body instanceof String) {
            return R.ok(body);
        }

        // Handle void return type
        if (body == null) {
            return R.ok();
        }

        // Wrap the response in R
        return R.ok(body);
    }

    private boolean shouldSkip(String path) {
        return path.contains("/actuator") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.contains(".") ||
               path.startsWith("/error");
    }
} 