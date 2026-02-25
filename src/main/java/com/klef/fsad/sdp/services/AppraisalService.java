package com.klef.fsad.sdp.services;

import java.util.List;
import com.klef.fsad.sdp.model.Appraisal;

public interface AppraisalService {
    String addAppraisal(Appraisal appraisal);
    List<Appraisal> viewAppraisalsByEmployee(Long empId);
    List<Appraisal> viewAppraisalsByManager(Long managerId);
}
