package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.model.Vote;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PutVoteCriteriaInfoRequest {
	@NotBlank
	private String name;

	@NotNull
	private int maxValue;

	private String description;

	private long trackId;

	private List<Vote> voteList;
}
