package com.programmers.voucher.core.exception;

public enum ExceptionMessage {

	EMPTY_BUFFER("입출력 버퍼가 비었습니다."),

	WRONG_COMMAND("명령어를 다시 입력해주세요."),
	WRONG_VOUCHER_TYPE("바우처 이름을 잘못 입력하셨습니다."),
	WRONG_DISCOUNT_TYPE("할인값을 숫자로 입력해주세요"),
	OUT_OF_DISCOUNT_AMOUNT_RANGE("할인 금액은 0보다 커야합니다."),
	OUT_OF_DISCOUNT_PERCENT_RANGE("할인 비율은 0보다 크고 100이하여야 합니다."),
	WRONG_CUSTOMER_TYPE("잘못된 고객 타입입니다."),

	DATA_UPDATE_FAIL("데이터 갱신에 실패했습니다."),
	VOUCHER_NOT_FOUND("해당되는 바우처가 없습니다."),
	CUSTOMER_NOT_FOUND("해당되는 고객이 없습니다");

	private String message;

	ExceptionMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
