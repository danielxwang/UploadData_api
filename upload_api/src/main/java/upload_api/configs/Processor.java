package upload_api.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import upload_api.model.Product;


public class Processor implements ItemProcessor<Product, Product>{
	private static final Logger log = LoggerFactory.getLogger(Processor.class);

    public Product process(final Product product) throws Exception {
        final String name = product.getProduct_name();
        final int id = product.getProduct_id();
        
        final Product newProduct = new Product(id, name);
        log.info("The product is: "+name);

        return newProduct;
    }
}
