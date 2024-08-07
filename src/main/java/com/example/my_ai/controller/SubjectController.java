package com.example.my_ai.controller;

import com.example.my_ai.domain.Member;
import com.example.my_ai.domain.Subject;
import com.example.my_ai.model.SubjectDTO;
import com.example.my_ai.repos.MemberRepository;
import com.example.my_ai.repos.SubjectRepository;
import com.example.my_ai.service.SubjectService;
import com.example.my_ai.util.CustomCollectors;
import com.example.my_ai.util.ReferencedWarning;
import com.example.my_ai.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;
    private final MemberRepository memberRepository;
    private final SubjectRepository subjectRepository;

    public SubjectController(final SubjectService subjectService,
            final MemberRepository memberRepository, final SubjectRepository subjectRepository) {
        this.subjectService = subjectService;
        this.memberRepository = memberRepository;
        this.subjectRepository = subjectRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("memberValues", memberRepository.findAll(Sort.by("username"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Member::getUsername, Member::getUsername)));
        model.addAttribute("parentValues", subjectRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Subject::getId, Subject::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "subject/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("subject") final SubjectDTO subjectDTO) {
        return "subject/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("subject") @Valid final SubjectDTO subjectDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "subject/add";
        }
        subjectService.create(subjectDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("subject.create.success"));
        return "redirect:/subjects";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("subject", subjectService.get(id));
        return "subject/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("subject") @Valid final SubjectDTO subjectDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "subject/edit";
        }
        subjectService.update(id, subjectDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("subject.update.success"));
        return "redirect:/subjects";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = subjectService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            subjectService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("subject.delete.success"));
        }
        return "redirect:/subjects";
    }

}
