package com.a6raywa1cher.hackservspring.rest.res;

import lombok.Data;

@Data
public class GetConfigResponse {

	private String maxFileSize;

	private Long minEmailReq;

	private Long maxEmailDuration;

	private String currentHackState;
}
