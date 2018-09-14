package com.batch.config;


import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FlowConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step stepFlow1() {
        return stepBuilderFactory.get("stepFlow1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Step1 from inside flow1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step stepFlow2() {
        return stepBuilderFactory.get("stepFlow2").tasklet((stepContribution, chunkContext) -> {
            System.out.println("Step2 from inside flow1");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Flow flow1() {
        FlowBuilder<Flow> flowFlowBuilder = new FlowBuilder<>("flow1");

        flowFlowBuilder
                .start(stepFlow1())
                .next(stepFlow2())
                .end();

        return flowFlowBuilder.build();
    }
}
