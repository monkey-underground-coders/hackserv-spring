package com.a6raywa1cher.hackservspring.dto.mapper;

import com.a6raywa1cher.hackservspring.dto.*;
import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.rest.req.PutTeamInfoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {EntityResolver.class})
public interface MapStructMapper {
	@Mapping(source = "team.id", target = "team")
	@Mapping(source = "request.id", target = "request")
	TeammateUser toTeammateUser(User user);

	MemberTeam toMemberTeam(Team team);

	PublicTeam toPublicTeam(Team team);

	ReferenceTrack toReferenceTrack(Track track);

	@Mapping(source = "request.trackId", target = "track")
	TeamInfo fromPutTeamInfoRequest(PutTeamInfoRequest request);
}
