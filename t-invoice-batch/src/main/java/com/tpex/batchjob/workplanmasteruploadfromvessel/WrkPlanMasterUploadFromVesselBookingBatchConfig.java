package com.tpex.batchjob.workplanmasteruploadfromvessel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.tpex.batchjob.JobCompletionNotificationListener;
import com.tpex.dto.UploadWrkPlanMasterFromVesselBookingRequest;
import com.tpex.dto.WrkPlanMasterUploadBatchInputDto;
import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.repository.CustomVesselBookingRepository;
import com.tpex.repository.InvGenWorkPlanMstRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@RequiredArgsConstructor
public class WrkPlanMasterUploadFromVesselBookingBatchConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final InvGenWorkPlanMstRepository invGenWorkPlanMstRepository;
	private final CustomVesselBookingRepository customVesselBookingRepository;
	private final JobCompletionNotificationListener jobCompletionNotificationListener;

	@Bean
	@Qualifier(value = "wrkPlanMasterUploadFromVesselBookingBatchJob")
	Job wrkPlanMasterUploadFromVesselBookingBatchJob() {

		return this.jobBuilderFactory.get("wrkPlanMasterUploadFromVesselBookingBatchJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobCompletionNotificationListener)
				.start(processWrkPlanMasterFromVesselBookingUploadBatch())
				.build();

	}

	public Step processWrkPlanMasterFromVesselBookingUploadBatch() {
		return this.stepBuilderFactory.get("processWrkPlanMasterFromVesselBookingUploadBatch")
				.<WrkPlanMasterUploadBatchInputDto, InvGenWorkPlanMstEntity>chunk(5)
				.reader(wrkPlanMasterFromVeseelRepoItemReader(null))
				.processor(wrkPlanMasterUploadFromVesselBookingBatchProcessor())
				.writer(wrkPlanMasterWriterFromVesselBooking())
				.listener(wrkPlanMasterUploadFromVesselBookingStepListener())
				.build();
	}

	@Bean
	@StepScope
	RepositoryItemReader<WrkPlanMasterUploadBatchInputDto> wrkPlanMasterFromVeseelRepoItemReader(
			@Value("#{jobParameters}") Map<String, Object> mapParams){
		UploadWrkPlanMasterFromVesselBookingRequest in = prepareInputReader(mapParams);
		RepositoryItemReader<WrkPlanMasterUploadBatchInputDto> reader = new RepositoryItemReader<>();
		reader.setRepository(customVesselBookingRepository);
		reader.setMethodName("getVesselBookingToUploadWorkPlanMaster");
		reader.setArguments(Arrays.asList(in));
		reader.setPageSize(100);
		Map<String, Direction> sorts = new HashMap<>();
		sorts.put("contDest", Direction.ASC);
		sorts.put("etd1", Direction.ASC);
		sorts.put("shipComp", Direction.ASC);
		reader.setSort(sorts);
		return reader;
	}

	private UploadWrkPlanMasterFromVesselBookingRequest prepareInputReader(Map<String, Object> mapParams) {
		UploadWrkPlanMasterFromVesselBookingRequest in = new UploadWrkPlanMasterFromVesselBookingRequest();
		if(mapParams != null) {
			in.setVanningMonth(mapParams.get("vanningMonth") != null ? (String) mapParams.get("vanningMonth") : null);
			in.setDestinationCode(mapParams.get("destinationCode") != null ? (String) mapParams.get("destinationCode") : null);
			in.setEtdFrom(mapParams.get("etdFrom") != null ? (String) mapParams.get("etdFrom") : null);
			in.setEtdTo(mapParams.get("etdTo") != null ? (String) mapParams.get("etdTo") : null);
			in.setShippingCompanyCode(mapParams.get("shippingCompanyCode") != null ? (String) mapParams.get("shippingCompanyCode") : null);
		}
		return in;
	}

	@Bean
	@StepScope
	ItemProcessor<WrkPlanMasterUploadBatchInputDto, InvGenWorkPlanMstEntity> wrkPlanMasterUploadFromVesselBookingBatchProcessor() {
		return new WrkPlanMasterUploadFromVesselBookingBatchProcessor();
	}

	@Bean
	@StepScope
	RepositoryItemWriter<InvGenWorkPlanMstEntity> wrkPlanMasterWriterFromVesselBooking() {
		RepositoryItemWriter<InvGenWorkPlanMstEntity> repositoryItemWriter = new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(invGenWorkPlanMstRepository);
		repositoryItemWriter.setMethodName("save");
		return repositoryItemWriter;
	}
	
	@Bean
	@StepScope
	StepExecutionListener wrkPlanMasterUploadFromVesselBookingStepListener() {
		return new WrkPlanMasterUploadFromVesselBookingStepListener();
	}

}
