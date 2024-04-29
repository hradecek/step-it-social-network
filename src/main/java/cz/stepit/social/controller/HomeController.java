package cz.stepit.social.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import cz.stepit.social.repository.MemberRepository;

@Controller
public class HomeController {

    protected final MemberRepository memberRepository;

    public HomeController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("members", memberRepository.findAll());
        return "index";
    }
}
