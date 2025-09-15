package com.tpex.serviceimpl;

import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.service.SendMonthlyContToBrokerService;
import com.tpex.util.ConstantUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SendMonthlyContToBrokerServiceImpl implements SendMonthlyContToBrokerService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("binf023Job")
    private Job binf023Job;

    private static final String VAN_MONTH = "mstrVanMonth";
    private static final String COMPANY_CODE = "companyCode";
    private static final String CB_CODE = "cbCd";
    @Async
    @Override
    public void sendMonthlyContToBrokerBatchJob(String vanMonth) {
        try {
            jobLauncher.run(binf023Job, getJobParameters(ConstantUtils.TMT,vanMonth,ConstantUtils.KTL));
            jobLauncher.run(binf023Job, getJobParameters(ConstantUtils.TMT,vanMonth,ConstantUtils.NYK));
            jobLauncher.run(binf023Job, getJobParameters(ConstantUtils.TMT,vanMonth,ConstantUtils.MOL));
            jobLauncher.run(binf023Job, getJobParameters(ConstantUtils.STM,vanMonth,ConstantUtils.KTL));
            jobLauncher.run(binf023Job, getJobParameters(ConstantUtils.STM,vanMonth,ConstantUtils.NYK));
            jobLauncher.run(binf023Job, getJobParameters(ConstantUtils.STM,vanMonth,ConstantUtils.MOL));

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new MyResourceNotFoundException();
        }

    }

    private JobParameters getJobParameters(String cmpCd, String vanMonth, String cbCd) {
        return new JobParametersBuilder()
                .addLong(ConstantUtils.JOB_P_START_AT, System.currentTimeMillis())
                .addString(ConstantUtils.JOB_P_BATCH_ID,ConstantUtils.BINF023)
                .addString(ConstantUtils.JOB_P_BATCH_NAME, ConstantUtils.BINF023NAME)
                .addString(COMPANY_CODE, cmpCd)
                .addString(VAN_MONTH, vanMonth)
                .addString(CB_CODE,cbCd)
                .toJobParameters();
    }
}
