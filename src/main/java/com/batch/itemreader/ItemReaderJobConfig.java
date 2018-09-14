package com.batch.itemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ItemReaderJobConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public StateLessItemReader stateLessItemReader() {
        List<String> data = new ArrayList<>();
        data.add("1");
        data.add("2");
        data.add("3");
        data.add("4");
        return new StateLessItemReader(data);
    }

    /*at a time 2 item read and write*/
    @Bean
    public Step stepI1() {
        return stepBuilderFactory.get("stepI1")
                .<String, String>chunk(2)
                .reader(stateLessItemReader())
                .writer(list -> {
                    for (Object obj : list) {
                        System.out.println("Writing data to list " + obj.toString());
                    }
                }).build();
    }

    @Bean
    public Job interfaceJob() {
        return jobBuilderFactory.get("interfaceJob")
                .start(stepI1())
                .build();
    }
}
