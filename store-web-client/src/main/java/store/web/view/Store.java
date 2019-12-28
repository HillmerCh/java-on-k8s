package store.web.view;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Named
@SessionScoped
public class Store implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private RestClient restClient = new RestClient();

	@NotNull
	@NotEmpty
	protected String name;
	@NotNull
	protected Double price;
	protected List<Product> productList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<Product> getProductList() {
		return productList;
	}

	@PostConstruct
	private void init() {
		try {
			this.getAllProducts();
		} catch (Exception ex) {
			logger.severe("Processing of HTTP response failed.");
			ex.printStackTrace();
		}
	}

	public void getAllProducts() {
		this.productList = this.restClient.getAllProductss();
	}

	public void addProduct() {
		this.restClient.addProduct( this.name, this.price);
		this.name = null;
		this.price = null;
		this.getAllProducts();
	}

	public void removeProduct(String productId) {
		this.restClient.removeProduct( productId );
		this.getAllProducts();
	}


}
