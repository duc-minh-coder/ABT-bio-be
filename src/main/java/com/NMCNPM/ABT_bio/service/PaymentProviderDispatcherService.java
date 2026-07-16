package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.enums.PaymentProviderEnum;
import com.NMCNPM.ABT_bio.exception.AppException;
import com.NMCNPM.ABT_bio.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentProviderDispatcherService {
    List<PaymentProviderServiceInterface> providers;

    public PaymentProviderServiceInterface get(PaymentProviderEnum provider) {
        return providers.stream()
                .filter(x -> x.provider() == provider)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PROVIDER_NOT_FOUND));
    }
}
