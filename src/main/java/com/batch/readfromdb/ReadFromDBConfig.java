package com.batch.readfromdb;

import com.batch.entity.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReadFromDBConfig {

    @Autowired
    public DataSource dataSource;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Step readFromDb() {
        return stepBuilderFactory.get("readFromDb")
                .<Customer, Customer>chunk(2)
                .reader(pagingItemReader())
                .writer(items -> {
                    for (Customer item : items) {
                        System.out.println(item.toString());
                    }
                }).build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(readFromDb())
                .build();
    }

    /*JdbcCursorItemReader not thread safe*/
    @Bean
    public JdbcCursorItemReader<Customer> cursorItemReader() {
        System.out.println("Going to read 2 record");
        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
        reader.setSql("select id, first_name, last_name, birthday from customer order by last_name, first_name");
        reader.setDataSource(this.dataSource);
        reader.setRowMapper(new RowMapper<Customer>() {
            @Override
            public Customer mapRow(ResultSet r, int i) throws SQLException {
                return new Customer(r.getLong("id"),
                        r.getString("first_name"),
                        r.getString("last_name"),
                        r.getDate("birthday"));
            }
        });
        return reader;
    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingItemReader() {

        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper((r, i) ->
                new Customer(r.getLong("id"),
                        r.getString("first_name"),
                        r.getString("last_name"),
                        r.getDate("birthday")));

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, first_name, last_name, birthday");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);
        return reader;
    }
}
