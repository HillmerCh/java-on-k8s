package store.testcases;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import store.web.view.Store;
import store.web.view.Product;
import store.web.view.RestClient;

import org.junit.Test;


public class ClientTest {

	private RestClient restClient = new RestClient();

	@Inject
	public Store store;

	private  String getAnyName(){
		MutableList<String> productNameMutableList  = Lists.mutable.of("Apple", "Pear", "Orange", "Grapefuit", "Mandarin", "Banana", "Mango","Berries", "Strawberry", "Raspberry", "Kiwifruit", "Watermelon");
		return productNameMutableList.shuffleThis().getAny();
	}

	private Double getAnyPrice(){
		Random random = new Random();
		return Math.ceil(random.doubles( 1D,10D ).findAny().getAsDouble());
	}

	@Test
	public void addProduct() {
		Response response =  this.restClient.addProduct( this.getAnyName(), this.getAnyPrice());

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	}

	private  Product getAnyProduct(){
		MutableList<Product> productMutableList  = Lists.adapt( this.restClient.getAllProductss( ));
		return productMutableList.shuffleThis().getAny();
	}

	@Test
	public void getProductById() {
		Product anyProduct = this.getAnyProduct();
		Long idProduct = anyProduct.getId();
		Product product = this.restClient.getProduct( idProduct );
		assertEquals( product.getId().longValue(), idProduct.longValue());
	}

	@Test
	public void getAllProducts() {
		List<Product> productList = this.restClient.getAllProductss( );
		assertEquals( true, productList!=null);
	}

}
