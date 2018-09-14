package com.batch.config;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowConfigurationFirstLast {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Step beforeFlowStep() {

        return stepBuilderFactory.get("beforeFlowStep")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("I am before flow step");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step afterFlowStep() {

        return stepBuilderFactory.get("afterFlowStep")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("I am after flow step");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Job jobWithFlowFirst(Flow flow) {

        return jobBuilderFactory.get("jobWithFlowFirst")
                .start(flow)
                .next(afterFlowStep())
                .end()
                .build();
    }

    /*To run flow last you can not use next()*/
    @Bean
    public Job jobWithFlowLast(Flow flow) {

        return jobBuilderFactory.get("jobWithFlowLast")
                .start(beforeFlowStep())
                .on("COMPLETED").to(flow)
                .end()
                .build();
    }
}
