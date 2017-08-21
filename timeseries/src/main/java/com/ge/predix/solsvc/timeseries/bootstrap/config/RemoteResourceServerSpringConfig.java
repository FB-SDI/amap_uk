/**
 * 
 */
package com.ge.predix.solsvc.timeseries.bootstrap.config;

/**
 * @author ramalapoli
 *
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Order(0)
@EnableResourceServer
public class RemoteResourceServerSpringConfig extends ResourceServerConfigurerAdapter {

//	@Autowired
//	AppCorsFilter corsFilter;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		
		System.out.println("configure #############" + http.getSharedObjects());
		http.csrf().disable();
		http.addFilterBefore(corsFilter(), ChannelProcessingFilter.class);
		
		http
	      // Enable csrf only on some request matches
	       
	        .authorizeRequests()
	        .antMatchers("/").permitAll()
	        .antMatchers("/login").permitAll()
	        .antMatchers("/getAssetDataByAssetId/**").permitAll()
	        .antMatchers("/ingestAssetData/**").permitAll()
            .anyRequest().access("#oauth2.hasScope('uaa.resource')"); 
		/*http.authorizeRequests()
		.antMatchers("/login","/getAssetDataByAssetId/**","/ingestAssetData/**").permitAll().
		.antMatchers("/**").
		authenticated().anyRequest().access("#oauth2.hasScope('uaa.resource')"); // [4]
		*/
		System.out.println("configure #############" + http.getSharedObjects());
		// @formatter:on
	}

	@Bean
	public AccessTokenConverter accessTokenConverter() {
		return new DefaultAccessTokenConverter();
	}

	@Bean
	public RemoteTokenServices remoteTokenServices(final @Value("https://53b73921-acac-43d0-bef2-a8ec78d216ec.predix-uaa.run.aws-usw02-pr.ice.predix.io/check_token") String checkTokenUrl,
			final @Value("test_client") String clientId,
			final @Value("test_client") String clientSecret) {
		final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		remoteTokenServices.setCheckTokenEndpointUrl(checkTokenUrl);
		remoteTokenServices.setClientId(clientId);
		remoteTokenServices.setClientSecret(clientSecret);
		remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
		return remoteTokenServices;
	}
	@Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
         resources.resourceId("uaa");
    }
	
	
	@Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // you USUALLY want this
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("POST");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
	
	/*@Bean
	public FilterRegistrationBean corsFilter() {
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    config.addAllowedOrigin("*");
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("OPTIONS");
	    config.addAllowedMethod("HEAD");
	    config.addAllowedMethod("GET");
	    config.addAllowedMethod("PUT");
	    config.addAllowedMethod("POST");
	    config.addAllowedMethod("DELETE");
	    config.addAllowedMethod("PATCH");
	    source.registerCorsConfiguration("/**", config);
	    // return new CorsFilter(source);
	    final FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
	    bean.setOrder(0);
	    return bean;
	}*/

}
