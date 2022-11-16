package com.programmers.voucher.domain.voucher.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.programmers.voucher.domain.voucher.model.Voucher;
import com.programmers.voucher.domain.voucher.model.VoucherType;
import com.programmers.voucher.domain.voucher.repository.VoucherRepository;

@SpringBootTest
class VoucherServiceTest {

	@Autowired
	VoucherService service;

	@Autowired
	VoucherRepository repository;

	@AfterEach
	void afterEach() {
		repository.clear();
	}

	@Test
	@DisplayName("바우처를 생성하면 생성과 저장이 성공한다.")
	void createVoucher() {
		VoucherType fixedType = VoucherType.FIXED;
		VoucherType percentType = VoucherType.PERCENT;
		String fixedAmount = "1000";
		String percentAmount = "10";

		Voucher fixedVoucher = service.createVoucher(fixedAmount, fixedType);
		Voucher percentVoucher = service.createVoucher(percentAmount, percentType);
		Voucher findFixed = repository.findByUUID(fixedVoucher.getVoucherId());
		Voucher findPercent = repository.findByUUID(percentVoucher.getVoucherId());

		Assertions.assertThat(findFixed.toString()).isEqualTo(fixedVoucher.toString());
		Assertions.assertThat(findPercent.toString()).isEqualTo(percentVoucher.toString());
	}
}