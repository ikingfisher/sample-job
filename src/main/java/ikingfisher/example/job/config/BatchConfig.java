package ikingfisher.example.job.config;

import ikingfisher.example.job.dao.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private Logger logger = LoggerFactory.getLogger(BatchConfig.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public FlatFileItemReader<User> reader() {
        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("users.csv")); // CSV 文件路径
        reader.setLinesToSkip(1); // 如果CSV有标题行，则跳过它
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"name", "age"}); // CSV 列名
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(User.class);
            }});
        }});
        return reader;
    }

    @Bean
    public UserItemProcessor processor() {
        return new UserItemProcessor();
    }

    public static class UserItemProcessor implements ItemProcessor<User, User> {
        @Override
        public User process(User user) throws Exception {
            if (user.getAge() >= 18) {
                return user;
            } else {
                return null;
            }
        }
    }

    @Bean
    public ItemWriter<User> writer() {
        return items -> {
            for (User user : items) {
                logger.info("user name: {}, age: {}", user.getName(), user.getAge());
            }
        };
    }

    @Bean
    public Step step1() {
        return new StepBuilder("stepOne", jobRepository)
                .<User, User>chunk(1, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job myJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("myJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    public Step stepOne() {
        return new StepBuilder("stepOne", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    logger.info(">>>>>>> Hello from StepOne! <<<<<<<");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
