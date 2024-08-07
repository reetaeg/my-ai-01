package com.example.my_ai.service;

import com.example.my_ai.domain.Member;
import com.example.my_ai.domain.Subject;
import com.example.my_ai.model.MemberDTO;
import com.example.my_ai.repos.MemberRepository;
import com.example.my_ai.repos.SubjectRepository;
import com.example.my_ai.util.NotFoundException;
import com.example.my_ai.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final SubjectRepository subjectRepository;

    public MemberService(final MemberRepository memberRepository,
            final SubjectRepository subjectRepository) {
        this.memberRepository = memberRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<MemberDTO> findAll() {
        final List<Member> members = memberRepository.findAll(Sort.by("username"));
        return members.stream()
                .map(member -> mapToDTO(member, new MemberDTO()))
                .toList();
    }

    public MemberDTO get(final String username) {
        return memberRepository.findById(username)
                .map(member -> mapToDTO(member, new MemberDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final MemberDTO memberDTO) {
        final Member member = new Member();
        mapToEntity(memberDTO, member);
        member.setUsername(memberDTO.getUsername());
        return memberRepository.save(member).getUsername();
    }

    public void update(final String username, final MemberDTO memberDTO) {
        final Member member = memberRepository.findById(username)
                .orElseThrow(NotFoundException::new);
        mapToEntity(memberDTO, member);
        memberRepository.save(member);
    }

    public void delete(final String username) {
        memberRepository.deleteById(username);
    }

    private MemberDTO mapToDTO(final Member member, final MemberDTO memberDTO) {
        memberDTO.setUsername(member.getUsername());
        memberDTO.setPassword(member.getPassword());
        memberDTO.setNickname(member.getNickname());
        memberDTO.setEmail(member.getEmail());
        memberDTO.setProfileImgUrl(member.getProfileImgUrl());
        return memberDTO;
    }

    private Member mapToEntity(final MemberDTO memberDTO, final Member member) {
        member.setPassword(memberDTO.getPassword());
        member.setNickname(memberDTO.getNickname());
        member.setEmail(memberDTO.getEmail());
        member.setProfileImgUrl(memberDTO.getProfileImgUrl());
        return member;
    }

    public boolean usernameExists(final String username) {
        return memberRepository.existsByUsernameIgnoreCase(username);
    }

    public boolean emailExists(final String email) {
        return memberRepository.existsByEmailIgnoreCase(email);
    }

    public ReferencedWarning getReferencedWarning(final String username) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Member member = memberRepository.findById(username)
                .orElseThrow(NotFoundException::new);
        final Subject memberSubject = subjectRepository.findFirstByMember(member);
        if (memberSubject != null) {
            referencedWarning.setKey("member.subject.member.referenced");
            referencedWarning.addParam(memberSubject.getId());
            return referencedWarning;
        }
        return null;
    }

}
