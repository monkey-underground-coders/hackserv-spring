package com.a6raywa1cher.hackservspring.service.dto;

import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.Vote;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class VoteCriteriaInfo {
	@NotBlank
	private String name;

	@NotBlank
	private int maxValue;

	private String description;

	private Track track;

	private List<Vote> voteList;
}
