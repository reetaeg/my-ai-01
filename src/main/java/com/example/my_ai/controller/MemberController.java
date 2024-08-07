package com.example.my_ai.controller;

import com.example.my_ai.model.MemberDTO;
import com.example.my_ai.service.MemberService;
import com.example.my_ai.util.ReferencedWarning;
import com.example.my_ai.util.WebUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("members", memberService.findAll());
        return "member/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("member") final MemberDTO memberDTO) {
        return "member/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("member") @Valid final MemberDTO memberDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "member/add";
        }
        memberService.create(memberDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("member.create.success"));
        return "redirect:/members";
    }

    @GetMapping("/edit/{username}")
    public String edit(@PathVariable(name = "username") final String username, final Model model) {
        model.addAttribute("member", memberService.get(username));
        return "member/edit";
    }

    @PostMapping("/edit/{username}")
    public String edit(@PathVariable(name = "username") final String username,
            @ModelAttribute("member") @Valid final MemberDTO memberDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "member/edit";
        }
        memberService.update(username, memberDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("member.update.success"));
        return "redirect:/members";
    }

    @PostMapping("/delete/{username}")
    public String delete(@PathVariable(name = "username") final String username,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = memberService.getReferencedWarning(username);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            memberService.delete(username);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("member.delete.success"));
        }
        return "redirect:/members";
    }

}
