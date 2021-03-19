package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.model.Track;
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

    @NotBlank
    private String description;

    @NotNull
    private Track track;

    @NotNull
    private List<Vote> voteList;
}
