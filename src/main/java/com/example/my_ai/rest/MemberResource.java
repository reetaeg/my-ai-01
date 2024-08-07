package com.example.my_ai.rest;

import com.example.my_ai.model.MemberDTO;
import com.example.my_ai.model.SubjectDTO;
import com.example.my_ai.service.MemberService;
import com.example.my_ai.service.SubjectService;
import com.example.my_ai.util.ReferencedException;
import com.example.my_ai.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberResource {

    private final MemberService memberService;
    private final SubjectService subjectService;

    public MemberResource(final MemberService memberService,
                          final SubjectService subjectService) {
        this.memberService = memberService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @GetMapping("/{username}")
    public ResponseEntity<MemberDTO> getMember(
            @PathVariable(name = "username") final String username) {
        return ResponseEntity.ok(memberService.get(username));
    }

    @GetMapping("/{username}/{favorite}")
    public ResponseEntity<List<SubjectDTO>> getMemberAndFavorite(
            @PathVariable(name = "username") final String username,@PathVariable(name = "favorite") final boolean favorite) {
        return ResponseEntity.ok(subjectService.findByMemberIdAndFavorite(username,favorite));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createMember(@RequestBody @Valid final MemberDTO memberDTO) {
        final String createdUsername = memberService.create(memberDTO);
        return new ResponseEntity<>('"' + createdUsername + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{username}")
    public ResponseEntity<String> updateMember(
            @PathVariable(name = "username") final String username,
            @RequestBody @Valid final MemberDTO memberDTO) {
        memberService.update(username, memberDTO);
        return ResponseEntity.ok('"' + username + '"');
    }

    @DeleteMapping("/{username}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteMember(
            @PathVariable(name = "username") final String username) {
        final ReferencedWarning referencedWarning = memberService.getReferencedWarning(username);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        memberService.delete(username);
        return ResponseEntity.noContent().build();
    }

}
