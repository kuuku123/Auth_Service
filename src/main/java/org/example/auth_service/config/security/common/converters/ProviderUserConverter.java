package org.example.auth_service.config.security.common.converters;

public interface ProviderUserConverter<T, R> {
    R convert(T t);
}
