package org.example.auth_service.security.oauth2.common.converters;

public interface ProviderUserConverter<T, R> {

  R convert(T t);
}
