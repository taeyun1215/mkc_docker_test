package com.mck.domain.alert;

import com.mck.domain.alert.response.AlertAllResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertServiceImpl implements AlertService {

    private final AlertRepo alertRepo;

    @Override
    public List<AlertAllResDto> getAlertList(String username) {
        List<AlertAllResDto> alert_list = alertRepo.getAlertByUsername(username);
        return alert_list;
    }

//    @Override
//    public Alert getAlert(String username) {
//        Alert alert = alertRepo.findByIdThenConfirmIsZero(username);
//        if (alert == null){
//            return null;
//        }
//        return alert;
//    }

    @Override
    public void updateAlert(Alert alert) {
        alertRepo.save(alert);
    }
}
