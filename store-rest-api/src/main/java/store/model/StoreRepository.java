package store.model;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import store.model.entity.Product;

@Repository
@Transactional(readOnly = false)
public class StoreRepository {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@PersistenceContext
	private EntityManager entityManager;

	public List<Product> getAllCoffees() {
		logger.log(Level.INFO, "Finding all products.");
		return this.entityManager.createNamedQuery("Product.findAllProducts", Product.class).getResultList();
	}

	public Product persistCoffee(Product product) {
		logger.log( Level.INFO, "Persisting the new product {0}.", product );
		this.entityManager.persist( product );
		return product;
	}

	public void removeProductById(Long productId) {
		logger.log(Level.INFO, "Removing the product identified by {0}.", productId);
		Product product = entityManager.find( Product.class, productId);
		this.entityManager.remove( product );
	}

	public Product findProductById(Long productId) {
		logger.log(Level.INFO, "Finding the product identified by {0}.", productId);
		return this.entityManager.find( Product.class, productId);
	}
}