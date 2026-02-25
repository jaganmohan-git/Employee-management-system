package com.klef.fsad.sdp.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.klef.fsad.sdp.model.Appraisal;
import com.klef.fsad.sdp.repository.AppraisalRepository;

@Service
public class AppraisalServiceImpl implements AppraisalService {

    @Autowired
    private AppraisalRepository appraisalRepository;

    @Override
    public String addAppraisal(Appraisal appraisal) {
        appraisalRepository.save(appraisal);
        return "Appraisal added successfully";
    }

    @Override
    public List<Appraisal> viewAppraisalsByEmployee(Long empId) {
        return appraisalRepository.findByEmployeeId(empId);
    }

    @Override
    public List<Appraisal> viewAppraisalsByManager(Long managerId) {
        return appraisalRepository.findByManagerId(managerId);
    }
}
