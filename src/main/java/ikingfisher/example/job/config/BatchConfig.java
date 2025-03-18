package ikingfisher.example.job.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Bean
    public Job myJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("myJob", jobRepository)
                .start(stepOne(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step stepOne(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepOne", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">>>>>>> Hello from StepOne! <<<<<<<");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
