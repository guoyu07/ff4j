package org.ff4j.web.api.test.it;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.ff4j.web.api.FF4jJacksonMapper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.swagger.jaxrs.json.JacksonJsonProvider;

public class SampleIntegrationTest {
    
    private static final String HOST = "localhost";
    
    private static final int PORT = 3388;
    
    private static WebappContext webappContext;
    
    private static Client client = null;;
    
    private static HttpServer server = null;
    
    @BeforeClass
    public static void initOnce()  
    throws IOException {
        // WebContext for testing
        webappContext = new WebappContext("Test Context");
        ServletRegistration servletRegistration = webappContext.addServlet( "jersey-servlet", ServletContainer.class);
        servletRegistration.setInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
        servletRegistration.setInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters",  "com.sun.jersey.api.container.filter.LoggingFilter");
        servletRegistration.setInitParameter("javax.ws.rs.Application", SampleFF4jJersey2Application.class.getCanonicalName());
        servletRegistration.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        servletRegistration.addMapping("/*");
        
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonJsonProvider.class);
        clientConfig.register(FF4jJacksonMapper.class);
        client = ClientBuilder.newClient(clientConfig);
        
        server = new HttpServer();
        NetworkListener listener = new NetworkListener("grizzly2", HOST, PORT);
        server.addListener(listener);
        webappContext.deploy(server);
        
    }
    
    @Before
    public void start() throws IOException {
        server.start();
    }
    
    @Test
    public void testFF4j() throws IOException {
        client.target("http://" + HOST + ":" + PORT).path("/ff4j").request(MediaType.APPLICATION_JSON).get();
    }
    
    @After
    public void stop() {
        server.shutdownNow();
    }

}
