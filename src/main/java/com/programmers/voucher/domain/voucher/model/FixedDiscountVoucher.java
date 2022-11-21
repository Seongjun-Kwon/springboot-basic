package com.programmers.voucher.domain.voucher.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.programmers.voucher.core.exception.ExceptionMessage;

public class FixedDiscountVoucher extends Voucher {

	private static final Logger log = LoggerFactory.getLogger(FixedDiscountVoucher.class);
	private static final double MIN_DISCOUNT = 0;

	public FixedDiscountVoucher(UUID voucherId, String discount,
		VoucherType voucherType, LocalDateTime createdAt) {
		super(voucherId, discount, voucherType, createdAt);
	}

	@Override
	protected double validateDiscount(String discount) {
		double parsedDiscount;
		try {
			parsedDiscount = Double.parseDouble(discount);
		} catch (NumberFormatException e) {
			log.error(ExceptionMessage.WRONG_DISCOUNT_TYPE.getMessage());
			throw new NumberFormatException(ExceptionMessage.WRONG_DISCOUNT_TYPE.getMessage());
		}

		if (parsedDiscount <= MIN_DISCOUNT) {
			log.error(ExceptionMessage.OUT_OF_DISCOUNT_AMOUNT_RANGE.getMessage());
			throw new IllegalArgumentException(ExceptionMessage.OUT_OF_DISCOUNT_AMOUNT_RANGE.getMessage());
		}
		return parsedDiscount;
	}

	@Override
	public String toString() {
		return "voucherId: " + voucherId + ", voucherType: " + voucherType.name() + ", discount: " + discount
			+ ", createdAt: " + createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
