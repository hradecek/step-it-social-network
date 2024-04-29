package cz.stepit.social.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.stepit.social.entities.Member;
import cz.stepit.social.repository.MemberRepository;

@Service
public class MemberService {

    protected final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Member getMember(long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new MemberNotFoundException(username));
    }

    @Transactional
    public void addFriend(long id, long friendId) {
        if (id == friendId) {
            throw new SelfFriendException();
        }
        final var member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
        final var friend = memberRepository.findById(friendId).orElseThrow(() -> new MemberNotFoundException(friendId));
        member.addFriend(friend);
        memberRepository.save(member);
    }
}
