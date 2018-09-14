package com.batch.config;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution s, ChunkContext c) throws Exception {
                        System.out.println(">> Job of step 1");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((s, c) -> {
                    System.out.println(">> Job of step 2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet((s, c) -> {
                    System.out.println(">>  Job of step 3");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job job1() {
        return jobBuilderFactory.get("job1")
                .start(step1())
                .next(step2())
                .next(step3())
                .next(step1())
                .build();
    }

    @Bean
    public Job transitionJob() {
        return jobBuilderFactory.get("transitionJob")
                .start(step1()).on("COMPLETE").to(step2())
                .from(step2()).on("COMPLETE").to(step3())
                .from(step3()).end()
                .build();
    }

    @Bean
    public Job transitionJobFailed() {
        return jobBuilderFactory.get("transitionJobFailed")
                .start(step1()).on("COMPLETE").to(step2())
                .from(step2()).on("COMPLETE").fail()
                .from(step3()).end()
                .build();
    }

    @Bean
    public Job transitionJobStopAndRestart() {
        return jobBuilderFactory.get("transitionJobStopAndRestart")
                .start(step1()).on("COMPLETE").to(step2())
                .from(step2()).on("COMPLETE").stopAndRestart(step3())
                .from(step3()).end()
                .build();
    }
}
