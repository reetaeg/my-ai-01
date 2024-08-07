package com.example.my_ai.service;

import com.example.my_ai.domain.Member;
import com.example.my_ai.domain.Subject;
import com.example.my_ai.model.SubjectDTO;
import com.example.my_ai.repos.MemberRepository;
import com.example.my_ai.repos.SubjectRepository;
import com.example.my_ai.util.NotFoundException;
import com.example.my_ai.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final MemberRepository memberRepository;

    public SubjectService(final SubjectRepository subjectRepository,
            final MemberRepository memberRepository) {
        this.subjectRepository = subjectRepository;
        this.memberRepository = memberRepository;
    }

    public List<SubjectDTO> findAll() {
        final List<Subject> subjects = subjectRepository.findAll(Sort.by("id"));
        return subjects.stream()
                .map(subject -> mapToDTO(subject, new SubjectDTO()))
                .toList();
    }

    public List<SubjectDTO> findByMemberIdAndFavorite(String memberId, final boolean favorite) {
        Member member = memberRepository.getById(memberId);
        final List<Subject> subjects = subjectRepository.findAllByMemberAndIsFavorite(member,favorite);
        return subjects.stream()
                .map(subject -> mapToDTO(subject, new SubjectDTO()))
                .toList();
    }

    public SubjectDTO get(final Long id) {
        return subjectRepository.findById(id)
                .map(subject -> mapToDTO(subject, new SubjectDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SubjectDTO subjectDTO) {
        final Subject subject = new Subject();
        mapToEntity(subjectDTO, subject);
        return subjectRepository.save(subject).getId();
    }

    public void update(final Long id, final SubjectDTO subjectDTO) {
        final Subject subject = subjectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(subjectDTO, subject);
        subjectRepository.save(subject);
    }

    public void delete(final Long id) {
        subjectRepository.deleteById(id);
    }

    private SubjectDTO mapToDTO(final Subject subject, final SubjectDTO subjectDTO) {
        subjectDTO.setId(subject.getId());
        subjectDTO.setPriority(subject.getPriority());
        subjectDTO.setDepth(subject.getDepth());
        subjectDTO.setSubject(subject.getSubject());
        subjectDTO.setDescription(subject.getDescription());
        subjectDTO.setIsDone(subject.getIsDone());
        subjectDTO.setIsDivide(subject.getIsDivide());
        subjectDTO.setIsFavorite(subject.getIsFavorite());
        subjectDTO.setMember(subject.getMember() == null ? null : subject.getMember().getUsername());
        subjectDTO.setParent(subject.getParent() == null ? null : subject.getParent().getId());
        return subjectDTO;
    }

    private Subject mapToEntity(final SubjectDTO subjectDTO, final Subject subject) {
        subject.setPriority(subjectDTO.getPriority());
        subject.setDepth(subjectDTO.getDepth());
        subject.setSubject(subjectDTO.getSubject());
        subject.setDescription(subjectDTO.getDescription());
        subject.setIsDone(subjectDTO.getIsDone());
        subject.setIsDivide(subjectDTO.getIsDivide());
        subject.setIsFavorite(subjectDTO.getIsFavorite());
        final Member member = subjectDTO.getMember() == null ? null : memberRepository.findById(subjectDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        subject.setMember(member);
        final Subject parent = subjectDTO.getParent() == null ? null : subjectRepository.findById(subjectDTO.getParent())
                .orElseThrow(() -> new NotFoundException("parent not found"));
        subject.setParent(parent);
        return subject;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Subject subject = subjectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Subject parentSubject = subjectRepository.findFirstByParentAndIdNot(subject, subject.getId());
        if (parentSubject != null) {
            referencedWarning.setKey("subject.subject.parent.referenced");
            referencedWarning.addParam(parentSubject.getId());
            return referencedWarning;
        }
        return null;
    }

}
