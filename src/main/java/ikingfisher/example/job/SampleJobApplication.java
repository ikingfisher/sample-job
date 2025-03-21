package ikingfisher.example.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class SampleJobApplication implements CommandLineRunner {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job myJob;

	public static void main(String[] args) {
		SpringApplication.run(SampleJobApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

		Arrays.stream(args)
				.forEach(arg -> {
					String[] parts = arg.split("=", 2); // 限制只分割一次
					if (parts.length == 2) {
						String paramName = parts[0];
						String paramValue = parts[1];

						try {
							// 尝试作为Long类型添加
							Long longValue = Long.parseLong(paramValue);
							jobParametersBuilder.addLong(paramName, longValue);
						} catch (NumberFormatException e1) {
							try {
								// 尝试作为Double类型添加
								Double doubleValue = Double.parseDouble(paramValue);
								jobParametersBuilder.addDouble(paramName, doubleValue);
							} catch (NumberFormatException e2) {
								// 默认作为String类型添加
								jobParametersBuilder.addString(paramName, paramValue);
							}
						}
					} else {
						// 如果不是键值对形式，则简单地将其作为一个字符串参数添加
						jobParametersBuilder.addString(arg, "true");
					}
				});

		JobParameters jobParameters = jobParametersBuilder.toJobParameters();
		jobLauncher.run(myJob, jobParameters);
	}
}
