package cz.stepit.social.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cz.stepit.social.repository.MemberRepository;

@Controller
@RequestMapping("/member/{id}")
public class MemberController {

    protected final MemberRepository memberRepository;

    /**
     * Constructor.
     *
     * @param memberRepository {@link MemberRepository}
     */
    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/friends")
    public String listFriends(@PathVariable long id, Model model) {
        final var member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
        model.addAttribute("member", member);
        return "friends";
    }

    @PostMapping("/addFriend")
    public  String addFriend(@PathVariable long id, @RequestParam long friendId, RedirectAttributes redirectAttributes) {
        final var member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
        final var friend = memberRepository.findById(friendId).orElseThrow(() -> new MemberNotFoundException(friendId));
        member.addFriend(friend);
        memberRepository.save(member);

        redirectAttributes.addFlashAttribute("info", "Added friend");
        return "redirect:/member/{id}/friends";
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public String handleError(Model model, MemberNotFoundException ex) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}
