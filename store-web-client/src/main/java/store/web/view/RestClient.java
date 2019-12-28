package store.web.view;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class RestClient {

	public static final int HTTP_CREATED = 201;
	private static final Logger logger = Logger.getLogger( MethodHandles.lookup().lookupClass().getName() );
	public static String REST_URI = "http://localhost:8080/store/product/v1";//For local testing
	private Client client = ClientBuilder.newClient();

	public RestClient() {
		if( System.getenv("JAVA_STORE_API_URI")!=null){
			REST_URI = System.getenv("JAVA_STORE_API_URI") ;
		}

		logger.log( Level.INFO, " Creating the RestClient with the URI {0} ",
					REST_URI
		);
	}

	public List<Product> getAllProductss() {
		return this.client.target( REST_URI ).path( "/" ).request( MediaType.APPLICATION_JSON )
				.get( new GenericType<List<Product>>() {
				} );
	}

	public Response addProduct(String name, Double price) {
		Product product = new Product( name, price );
		return this.client.target( REST_URI ).path( "/" ).request( MediaType.APPLICATION_JSON ).post( Entity.json( product ) );
	}

	public void removeProduct(String productId) {
		this.client.target( REST_URI ).path( productId ).request().delete();
	}


	public Product getProduct(Long productId) {
		return this.client.target( REST_URI )
				.path( String.valueOf( productId ) )
				.request( MediaType.APPLICATION_JSON )
				.get( Product.class );
	}
}
