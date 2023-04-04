package com.mck.domain.alert;

import com.mck.domain.alert.response.AlertAllResDto;
import com.mck.domain.comment.request.CommentDto;
import com.mck.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public interface AlertService {
    List<AlertAllResDto> getAlertList(String username);

//    Alert getAlert(String usernmame);

    void updateAlert(Alert alert);
}
