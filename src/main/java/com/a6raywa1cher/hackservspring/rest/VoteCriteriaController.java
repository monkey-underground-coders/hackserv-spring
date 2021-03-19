package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.VoteCriteria;
import com.a6raywa1cher.hackservspring.rest.exc.VoteCriteriaNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateVoteCriteriaRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutVoteCriteriaInfoRequest;
import com.a6raywa1cher.hackservspring.service.VoteCriteriaService;
import com.a6raywa1cher.hackservspring.service.dto.VoteCriteriaInfo;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/criteria")
@Transactional(rollbackOn = Exception.class)
public class VoteCriteriaController {
    VoteCriteriaService criteriaService;

    public VoteCriteriaController(VoteCriteriaService criteriaService) {
        this.criteriaService = criteriaService;
    }

    @GetMapping("/{criteriaid}")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @JsonView(Views.DetailedInternal.class)
    public ResponseEntity<VoteCriteria> getCriteria(@PathVariable long criteriaid) throws VoteCriteriaNotExistsException {
        Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
        if (optionalVoteCriteria.isEmpty()) {
            throw new VoteCriteriaNotExistsException();
        }
        VoteCriteria criteria = optionalVoteCriteria.get();

        return ResponseEntity.ok(criteria);
    }

    @PutMapping(path="/{criteriaid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @JsonView(Views.DetailedInternal.class)
    public ResponseEntity<VoteCriteria> editCriteriaInfo(@PathVariable Long criteriaid, @RequestBody PutVoteCriteriaInfoRequest request) throws VoteCriteriaNotExistsException {
        Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
        if (optionalVoteCriteria.isEmpty()) {
            throw new VoteCriteriaNotExistsException();
        }

        VoteCriteriaInfo info = new VoteCriteriaInfo();
        BeanUtils.copyProperties(request, info);

        VoteCriteria criteria = criteriaService.editCriteriaInfo(optionalVoteCriteria.get(), info);

        return ResponseEntity.ok(criteria);
    }

    @PostMapping(path="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @JsonView(Views.DetailedInternal.class)
    public ResponseEntity<VoteCriteria> createCriteria(@RequestBody CreateVoteCriteriaRequest request) {
        VoteCriteria criteria = criteriaService.create(request.getName(), request.getMaxValue());
        return ResponseEntity.ok(criteria);
    }

    @DeleteMapping(path="/{criteriaid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @JsonView(Views.DetailedInternal.class)
    public ResponseEntity<VoteCriteria> deleteCriteria(@PathVariable Long criteriaid) throws VoteCriteriaNotExistsException {
        Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
        if (optionalVoteCriteria.isEmpty()) {
            throw new VoteCriteriaNotExistsException();
        }
        VoteCriteria criteria = optionalVoteCriteria.get();
        criteriaService.deleteCriteria(criteria);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/criteria")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @JsonView(Views.Internal.class)
    public ResponseEntity<List<VoteCriteria>> getAllCriteria() {
        List<VoteCriteria> criteriaList = criteriaService.getAllCriteria().collect(Collectors.toList());
        return ResponseEntity.ok(criteriaList);
    }
}
