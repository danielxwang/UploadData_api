package upload_api.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upload_api.model.Product;

@RestController
public class Controller {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@CrossOrigin
	@RequestMapping("/api/v1/products")
	public List<Product> getAllAgencies() {
		return jdbcTemplate.query("SELECT * FROM product", new RowMapper<Product>() {

			public Product mapRow(ResultSet rs, int arg1) throws SQLException {
				Product p = new Product();
				p.setProduct_id(rs.getInt("product_id"));
				p.setProduct_name(rs.getString("product_name"));

				return p;
			}
		});
	}
}
