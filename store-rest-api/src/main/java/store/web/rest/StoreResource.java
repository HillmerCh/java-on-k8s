package store.web.rest;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.persistence.PersistenceException;

import store.model.StoreRepository;
import store.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/store/product/v1")
public class StoreResource {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Autowired
	private StoreRepository storeRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Product> getAllCoffees() {
		return this.storeRepository.getAllCoffees();
	}

	@PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		try {
			product = this.storeRepository.persistCoffee( product );

			return new ResponseEntity<Product>( product, HttpStatus.CREATED);

		} catch (PersistenceException e) {
			logger.log(Level.SEVERE, "Error creating product {0}: {1}.", new Object[] { product, e });
			//throw new WebApplicationException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)

	public Product getProductById(@PathVariable("id") Long productId) {
		return this.storeRepository.findProductById( productId);
	}

	@DeleteMapping("/{id}")
	public void deleteProduct(@PathVariable("id") Long productId) {

		try {
			this.storeRepository.removeProductById( productId);
		} catch (IllegalArgumentException ex) {
			logger.log(Level.SEVERE, "Error calling deleteProduct() for productId {0}: {1}.",
					new Object[] { productId, ex });
			//throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
}