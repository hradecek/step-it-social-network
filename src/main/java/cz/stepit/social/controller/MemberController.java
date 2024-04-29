package cz.stepit.social.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cz.stepit.social.service.MemberNotFoundException;
import cz.stepit.social.service.MemberService;
import cz.stepit.social.service.SelfFriendException;

@Controller
@RequestMapping("/member/{id}")
public class MemberController {

    protected final MemberService memberService;

    /**
     * Constructor.
     *
     * @param memberService {@link cz.stepit.social.service.MemberService}
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PreAuthorize("@securityService.isOwner(#id)")
    @GetMapping("/friends")
    public String listFriends(@PathVariable long id, Model model) {
        model.addAttribute("member", memberService.getMember(id));
        return "friends";
    }

    @PostMapping("/addFriend")
    public  String addFriend(@PathVariable long id, @RequestParam long friendId, RedirectAttributes redirectAttributes) {
        memberService.addFriend(id, friendId);

        redirectAttributes.addFlashAttribute("info", "Added friend");
        return "redirect:/member/{id}/friends";
    }

    @ExceptionHandler({MemberNotFoundException.class, SelfFriendException.class})
    public String handleError(Model model, Throwable ex) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}
