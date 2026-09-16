package dev.markodojkic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

@Configuration
@Conditional(LdapEnabledCondition.class)
public class LdapConfig {
    @Bean
    LdapContextSource ldapContextSource(
            @Value("${app.ldap.url}") String url,
            @Value("${app.ldap.base}") String base,
            @Value("${app.ldap.manager-dn}") String managerDn,
            @Value("${app.ldap.manager-password}") String managerPassword) {
        LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setBase(base);
        source.setUserDn(managerDn);
        source.setPassword(managerPassword);
        source.afterPropertiesSet();
        return source;
    }

    @Bean
    LdapAuthenticationProvider ldapAuthenticationProvider(LdapContextSource contextSource) {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(new FilterBasedLdapUserSearch("", "(uid={0})", contextSource));
        DefaultLdapAuthoritiesPopulator authorities =
                new DefaultLdapAuthoritiesPopulator(contextSource, "ou=groups");
        return new LdapAuthenticationProvider(authenticator, authorities);
    }
}
