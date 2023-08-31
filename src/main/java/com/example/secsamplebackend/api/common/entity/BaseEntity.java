package com.example.secsamplebackend.api.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity implements Serializable {

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createAt;

	@LastModifiedDate
	@Column(name = "last_modified_at")
	private LocalDateTime updateAt;

	@Column(name = "is_valid")
	@Builder.Default()
	private String isValid = "Y";

}
