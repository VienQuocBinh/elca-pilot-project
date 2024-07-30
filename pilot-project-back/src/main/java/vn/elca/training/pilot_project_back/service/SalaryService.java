package vn.elca.training.pilot_project_back.service;

import org.springframework.data.domain.Page;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.proto.salary.SalaryListRequest;

public interface SalaryService {
    Page<SalaryResponseDto> getSalariesByEmployerId(SalaryListRequest request);

    //    @Scheduled(cron = "${salary.csv.process.cron}")
//    @Scheduled(fixedDelay = 5000)
    void processSalaryCsvFilesJob();
}
