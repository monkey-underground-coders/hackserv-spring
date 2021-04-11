package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.VoteCriteria;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class PutTrackRequest {
	@NotBlank
	private String trackName;

	private List<VoteCriteria> criteriaList;

	private List<Team> teams;
}
