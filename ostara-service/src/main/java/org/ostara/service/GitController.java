/**
 * 
 */
package org.ostara.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.ostara.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ramahadevan
 */

@Path("/git")
public class GitController {
	private static Logger logger = LoggerFactory.getLogger(GitController.class);
	
	@GET
	@Path(value = "{org}/{repo}/branches")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object branches(@QueryParam("org") String organization, @QueryParam("repo") String repository, @QueryParam("page") int page, @QueryParam("per_page") int perPage) {
		return callGitHub(organization, repository, "branches", "?page=" + page + "&per_page=" + perPage);
	}
	
	@GET
	@Path(value = "{org}/{repo}/contents")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object contents(@QueryParam(value="org") String organization, @QueryParam(value="repo") String repository, 
			@QueryParam(value="path") String path, @QueryParam(value="branch") String branch) {

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
	        
	        if (response.getStatus() != 200) {
		    	logger.warn("Response status is NOT OK (" + response.getStatus() + ") for URL: " + url);
		    	return null;
		    }
	        
	        String value = response.readEntity(String.class);
	        response.close();  // You should close connections!
			
	        return value;
	    } catch (Throwable e) {
	    	logger.warn("Error while validating user input:",e);
		    return null;
	    }
	}
}
