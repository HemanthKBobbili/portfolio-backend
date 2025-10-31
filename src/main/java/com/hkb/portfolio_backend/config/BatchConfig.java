package com.hkb.portfolio_backend.config;

import com.hkb.portfolio_backend.entity.Expense;
import com.hkb.portfolio_backend.repository.ExpenseRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import java.io.File;

@Configuration
public class BatchConfig {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private PlatformTransactionManager transactionManager;

    // ItemReader: Reads CSV
    @Bean
    public FlatFileItemReader<Expense> itemReader() {
        FlatFileItemReader<Expense> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("uploads/expenses.csv"));  // Temp file path
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);  // Skip header
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Expense> lineMapper() {
        DefaultLineMapper<Expense> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("description", "amount", "date", "category");

        BeanWrapperFieldSetMapper<Expense> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Expense.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    // ItemProcessor: Validates/processes data
    @Bean
    public ItemProcessor<Expense, Expense> itemProcessor() {
        return expense -> {
            // Add userId (from job parameter or context; placeholder for now)
            expense.setUser(new com.hkb.portfolio_backend.entity.User());
            expense.getUser().setId(1L);  // Set to current user; enhance later
            return expense;
        };
    }

    // ItemWriter: Saves to DB
    @Bean
    public RepositoryItemWriter<Expense> itemWriter() {
        RepositoryItemWriter<Expense> writer = new RepositoryItemWriter<>();
        writer.setRepository(expenseRepository);
        writer.setMethodName("save");
        return writer;
    }

    // Step: Combines reader, processor, writer
    @Bean
    public Step step1() {
        return new StepBuilder("csv-step", jobRepository)
                .<Expense, Expense>chunk(10, transactionManager)  // Process 10 items at a time
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    // Job: Defines the batch process
    @Bean
    public Job importExpensesJob() {
        return new JobBuilder("importExpensesJob", jobRepository)
                .start(step1())
                .build();
    }
}
