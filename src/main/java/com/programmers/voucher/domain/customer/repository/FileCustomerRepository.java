package com.programmers.voucher.domain.customer.repository;

import static com.programmers.voucher.core.exception.ExceptionMessage.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.programmers.voucher.core.exception.EmptyBufferException;
import com.programmers.voucher.core.exception.NotFoundException;
import com.programmers.voucher.domain.customer.model.Customer;
import com.programmers.voucher.domain.customer.model.CustomerType;

@Repository
@Profile("file")
public class FileCustomerRepository implements CustomerRepository {

	private static final Logger log = LoggerFactory.getLogger(FileCustomerRepository.class);
	private static final Map<UUID, Customer> customers = new LinkedHashMap();
	private static final String LINE_SEPARATOR = ", |: ";
	private final String filePath;

	public FileCustomerRepository(@Value("${repository.file.blacklist}") String filePath) {
		this.filePath = filePath;
	}

	@PostConstruct
	void readFile() {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] customerInfo = line.split(LINE_SEPARATOR);
				UUID customerId = UUID.fromString(customerInfo[1]);
				CustomerType customerType = CustomerType.getCustomerType(customerInfo[3]);
				String createdAt = customerInfo[5];
				String lastModifiedAt = customerInfo[7];

				Customer customer = new Customer(customerId,
					LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
					customerType,
					LocalDateTime.parse(lastModifiedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				customers.put(customerId, customer);
			}
		} catch (IOException e) {
			log.error(EMPTY_BUFFER.getMessage());
			throw new EmptyBufferException();
		}
	}

	@PreDestroy
	void writeFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
			for (Customer customer : customers.values()) {
				writer.write(customer.toString() + System.lineSeparator());
				writer.flush();
			}
		} catch (IOException e) {
			log.error(EMPTY_BUFFER.getMessage());
			throw new EmptyBufferException();
		}
	}

	@Override
	public Customer save(Customer customer) {
		customers.put(customer.getCustomerId(), customer);
		return customer;
	}

	@Override
	public Customer findById(UUID customerId) {
		return Optional.ofNullable(customers.get(customerId))
			.orElseThrow(() -> {
				log.error(CUSTOMER_NOT_FOUND.getMessage());
				throw new NotFoundException(CUSTOMER_NOT_FOUND.getMessage());
			});
	}

	@Override
	public Customer update(Customer updateCustomer) {
		Optional.ofNullable(customers.get(updateCustomer.getCustomerId()))
			.ifPresentOrElse(customer -> customers.put(updateCustomer.getCustomerId(), customer),
				() -> {
					log.error(CUSTOMER_NOT_FOUND.getMessage());
					throw new NotFoundException(CUSTOMER_NOT_FOUND.getMessage());
				}
			);

		return updateCustomer;
	}

	@Override
	public void deleteById(UUID customerId) {
		Optional.ofNullable(customers.get(customerId))
			.ifPresentOrElse(customer -> customers.remove(customerId),
				() -> {
					log.error(CUSTOMER_NOT_FOUND.getMessage());
					throw new NotFoundException(CUSTOMER_NOT_FOUND.getMessage());
				}
			);
	}

	@Override
	public List<Customer> findAll() {
		return new ArrayList<>(customers.values());
	}

	@Override
	public List<Customer> findAllBlacklist() {
		return customers.values().stream()
			.filter(customer -> customer.getCustomerType().equals(CustomerType.BLACKLIST))
			.collect(Collectors.toList());
	}

	@Override
	public void clear() {
		customers.clear();
	}
}
