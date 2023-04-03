package com.mck.domain.alert;

import com.mck.domain.alert.response.AlertAllResDto;
import com.mck.domain.user.dto.UserProfileDto;
import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
@Slf4j
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<ReturnObject> getAlertAll(@AuthenticationPrincipal String username){
        ReturnObject returnObject;
        ErrorObject errorObject;

        List<AlertAllResDto> alert_list = alertService.getAlertList(username);

        returnObject = ReturnObject.builder().success(true).data(alert_list).build();

        return ResponseEntity.ok().body(returnObject);
    }

    @PostMapping
    public ResponseEntity<ReturnObject> confirmAlert(String id){
        ReturnObject returnObject;
        ErrorObject errorObject;

        Alert alert = alertService.getAlert(id);
        if (alert == null){
            errorObject = ErrorObject.builder().message("알림이 없습니다.").code("notfound_alert").build();
            ArrayList<ErrorObject> errors = new ArrayList<>();
            errors.add(errorObject);
            ReturnObject object = ReturnObject.builder().success(false).error(errors).build();
            return ResponseEntity.ok().body(object);
        }

        alert.setConfirm(1);

        alertService.updateAlert(alert);

        returnObject = ReturnObject.builder().success(true).build();

        return ResponseEntity.ok().body(returnObject);
    }

}
