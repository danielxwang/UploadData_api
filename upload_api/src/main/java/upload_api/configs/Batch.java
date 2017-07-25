package upload_api.configs;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import upload_api.model.Product;

@Configuration
@EnableBatchProcessing
public class Batch {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	// tag::readerwriterprocessor[]
	@Bean
	public FlatFileItemReader<Product> reader() {
		FlatFileItemReader<Product> reader = new FlatFileItemReader<Product>();
		reader.setResource(new FileSystemResource("product_data.csv"));
		reader.setLineMapper(lineMapper());

		return reader;
	}

	public LineMapper<Product> lineMapper() {
		DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<Product>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames(new String[] { "product_id", "product_name" });
		lineTokenizer.setIncludedFields(new int[] { 0, 1 });
		BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<Product>();
		fieldSetMapper.setTargetType(Product.class);
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

	@Bean
	public Processor processor() {
		return new Processor();
	}

	@Bean
	public JdbcBatchItemWriter<Product> writer() {
		JdbcBatchItemWriter<Product> writer = new JdbcBatchItemWriter<Product>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Product>());
		writer.setSql("INSERT INTO product (product_id, product_name) VALUES (:product_id, :product_name)");
		writer.setDataSource(dataSource);
		return writer;
	}
	// end::readerwriterprocessor[]

	// created a JobExecutionListener to monitor the job
	// tag::listener[]

	@Bean
	public JobExecutionListener listener() {
		return new JobCompletionNotificationListener(new JdbcTemplate(dataSource));
	}

	// end::listener[]

	// created a job
	// tag::jobstep[]
	@Bean
	public Job importUserJob() {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener())
				.flow(step1()).end().build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Product, Product> chunk(10).reader(reader()).processor(processor())
				.writer(writer()).build();
	}
	// end::jobstep[]
}
