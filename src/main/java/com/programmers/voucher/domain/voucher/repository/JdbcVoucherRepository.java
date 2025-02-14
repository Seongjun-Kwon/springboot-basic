package com.programmers.voucher.domain.voucher.repository;

import static com.programmers.voucher.core.exception.ExceptionMessage.*;
import static com.programmers.voucher.core.util.JdbcTemplateUtil.*;
import static com.programmers.voucher.domain.voucher.repository.VoucherSQL.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.programmers.voucher.core.exception.DataUpdateException;
import com.programmers.voucher.core.exception.NotFoundException;
import com.programmers.voucher.domain.voucher.model.Voucher;

@Repository
@Profile({"jdbc", "test"})
public class JdbcVoucherRepository implements VoucherRepository {

	private static final Logger log = LoggerFactory.getLogger(JdbcVoucherRepository.class);
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public JdbcVoucherRepository(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public Voucher save(Voucher voucher) {
		int save = jdbcTemplate.update(INSERT.getSql(), toVoucherParamMap(voucher));

		if (save != 1) {
			log.error(DATA_UPDATE_FAIL.getMessage());
			throw new DataUpdateException();
		}
		return voucher;
	}

	@Override
	public Voucher findById(UUID voucherId) {
		return Optional.ofNullable(
				jdbcTemplate.queryForObject(SELECT_BY_ID.getSql(), toVoucherIdMap(voucherId), voucherRowMapper))
			.orElseThrow(() -> {
				log.error(VOUCHER_NOT_FOUND.getMessage());
				throw new NotFoundException(VOUCHER_NOT_FOUND.getMessage());
			});
	}

	@Override
	public void deleteById(UUID voucherId) {
		int delete = jdbcTemplate.update(DELETE_BY_ID.getSql(), toVoucherIdMap(voucherId));

		if (delete != 1) {
			log.error(VOUCHER_NOT_FOUND.getMessage());
			throw new NotFoundException(VOUCHER_NOT_FOUND.getMessage());
		}
	}

	@Override
	public List<Voucher> findAll() {
		return jdbcTemplate.query(SELECT_ALL.getSql(), voucherRowMapper);
	}

	@Override
	public void clear() {
		jdbcTemplate.update(DELETE_ALL.getSql(), Collections.emptyMap());
	}
}
