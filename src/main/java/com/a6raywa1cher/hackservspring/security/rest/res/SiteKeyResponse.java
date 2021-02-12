package com.a6raywa1cher.hackservspring.security.rest.res;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SiteKeyResponse {
	@JsonView(Views.Public.class)
	private String siteKey;
}
