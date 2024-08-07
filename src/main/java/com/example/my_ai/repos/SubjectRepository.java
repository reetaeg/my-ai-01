package com.example.my_ai.repos;

import com.example.my_ai.domain.Member;
import com.example.my_ai.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Subject findFirstByMember(Member member);

    Subject findFirstByParentAndIdNot(Subject subject, final Long id);

    List<Subject> findAllByMemberAndIsFavorite(Member member, Boolean isFavorite);

}
