package com.ibs.donation.exception;

import com.ibs.global.exception.BusinessException;
import com.ibs.global.exception.ErrorCode;

public class TossPaymentsApiException extends BusinessException {

    public TossPaymentsApiException(String message) {
        super(ErrorCode.PAYMENT_CONFIRM_FAILED, message);
    }
}
