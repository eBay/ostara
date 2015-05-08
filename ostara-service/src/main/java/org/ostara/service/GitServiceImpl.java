/**
 * 
 */
package org.ostara.service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.ostara.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ramahadevan
 */


public class GitServiceImpl implements GitService {
	private static Logger logger = LoggerFactory.getLogger(GitServiceImpl.class);
	
	@Override
	public Object branches(String organization, String repository, int page, int perPage) {
		return callGitHub(organization, repository, "branches", "?page=" + page + "&per_page=" + perPage);
	}
	
	@Override
	public Object contents(String organization, String repository, String path,
			String branch) {
		organization = StringUtils.trimToEmpty(organization);
		repository = StringUtils.trimToEmpty(repository);
		path = StringUtils.trimToEmpty(path);
		branch = StringUtils.trimToEmpty(branch);
		
		// Default to pom.xml if unspecified
		if(!path.endsWith(".xml")) {
			if(!path.endsWith("/") && !path.isEmpty()) {
				path += "/";
			}
			
			path += "pom.xml";
		}
		
		return callGitHub(organization, repository, "contents", "/" + path + "?ref="+branch);
	}

	private Object callGitHub(String organization, String repository, String command, String param) {
		try {
			String url = String.format("%s/repos/%s/%s/%s%s", Config.getInstance().getGitAPIUrl(), organization, repository, command, param != null? param : "");
			Client client = ClientBuilder.newBuilder().build();
			
	        WebTarget target = client.register(new Authenticator(Config.getInstance().getUserName(), Config.getInstance().getPassword())).target(url);
	        Response response = target.request().get();
	        String value = response.readEntity(String.class);
	        
	        if (response.getStatus() != 200) {
		    	logger.warn("Response status is NOT OK (" + response.getStatus() + ", " + response.getStatusInfo() + ") for URL: " + url + "\nFull message: " + value);
		    	return null;
		    } else {
		    	logger.info("Response OK");
		    }
	        
	        response.close();  // You should close connections!
			
	        return value;
	    } catch (Throwable e) {
	    	logger.warn("Error while validating user input:",e);
		    return null;
	    }
	}
}
