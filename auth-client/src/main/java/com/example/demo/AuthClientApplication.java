package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableOAuth2Client
@RestController
public class AuthClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthClientApplication.class, args);
	}

	@Value("${oauth.resource:http://localhost:8080}")
	private String baseUrl;

	@Value("${oauth.authorize:http://localhost:8080/oauth/authorize}")
	private String authorizeUrl;

	@Value("${oauth.token:http://localhost:8080/oauth/token}")
	private String tokenUrl;

	@Autowired
	private OAuth2RestOperations restTemplate;

	@RequestMapping("/home")
	public List<Map<String,?>> home() {
		@SuppressWarnings("unchecked")
		List<Map<String,?>> result = restTemplate.getForObject(baseUrl + "/admin/beans", List.class);
		return result;
	}


	@RequestMapping("/mod")
	public String mod() {
		String result = restTemplate.getForObject(baseUrl + "/", String.class);
		return result;
	}

	@Bean
	public OAuth2RestOperations restTemplate(OAuth2ClientContext oauth2ClientContext) {
		return new OAuth2RestTemplate(resource(), oauth2ClientContext);
	}

	@Bean
	protected ClientCredentialsResourceDetails resource() {
		ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setGrantType("client_credentials");
		resource.setAccessTokenUri(tokenUrl);
		resource.setClientId("my-client-with-secret");
		resource.setClientSecret("secret");
/*
		List<String> scopes = new ArrayList<>();
		scopes.add("custom_mod");
		resource.setScope(scopes);
*/
		return resource ;
	}


	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.authorizeRequests()
					.antMatchers("/", "/home", "/mod").permitAll();
		}
	}
}
