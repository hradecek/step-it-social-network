package cz.stepit.social.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cz.stepit.social.service.AvatarUploadException;
import cz.stepit.social.service.ChatMessage;
import cz.stepit.social.service.MemberNotFoundException;
import cz.stepit.social.service.MemberService;
import cz.stepit.social.service.SelfFriendException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/member/{id}")
public class MemberController {

    protected static final String DEFAULT_AVATAR = "/default_avatar.png";

    protected final MemberService memberService;

    /**
     * Constructor.
     *
     * @param memberService {@link MemberService}
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PreAuthorize("@securityService.isOwner(#id)")
    @GetMapping("/friends")
    public String listFriends(@PathVariable long id, Model model) {
        final var member = memberService.getMember(id);
        model.addAttribute("member", member);
        model.addAttribute("avatarUri", member.getAvatarUri().map(avatar -> "/uploads/" + avatar).orElse(DEFAULT_AVATAR));
        return "friends";
    }

    @PostMapping("/avatar")
    public String avatar(@PathVariable long id, @RequestParam("avatar") MultipartFile avatar) {
        memberService.updateAvatar(id, avatar);

        return "redirect:/member/{id}/friends";
    }

    @PostMapping("/addFriend")
    public String addFriend(@PathVariable long id, @RequestParam long friendId, RedirectAttributes redirectAttributes) {
        memberService.addFriend(id, friendId);

        redirectAttributes.addFlashAttribute("info", "Added friend");
        return "redirect:/member/{id}/friends";
    }

    @GetMapping("/chat")
    public String getChat(Model model) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var username = authentication.getName();
        model.addAttribute("username", username);
        return "chat";
    }

    @MessageMapping("/chat")
    @SendTo("/chat/messages")
    public ChatMessage processMessageFromClient(ChatMessage chatMessage) {
        chatMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd. MM. yyyy HH:mm:ss")));
        return chatMessage;
    }

    @ExceptionHandler({AvatarUploadException.class, MemberNotFoundException.class, SelfFriendException.class})
    public String handleError(Model model, Throwable ex) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}
