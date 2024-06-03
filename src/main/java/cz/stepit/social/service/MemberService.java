package cz.stepit.social.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cz.stepit.social.entities.Member;
import cz.stepit.social.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);

    protected final MemberRepository memberRepository;
    protected final String uploadDir;

    public MemberService(MemberRepository memberRepository, @Value("${upload.dir}") String uploadDir) {
        this.memberRepository = memberRepository;
        this.uploadDir = uploadDir;
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

    @Transactional
    public void updateAvatar(long id, MultipartFile avatar) {
        final var avatarFileName = "avatar_" + id + "." + getFileExtension(avatar);
        final var uri = uploadDir + "/" + avatarFileName;
        final Path avatarPath = Paths.get(uri);

        try {
            final var bytes = avatar.getBytes();
            Files.write(avatarPath, bytes);
            final var member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
            member.setAvatarUri(avatarFileName);
            memberRepository.save(member);
        } catch (IOException ex) {
            try {
                Files.deleteIfExists(avatarPath);
            } catch (IOException __) {
                // OK
            }
            LOGGER.error("Cannot update avatar", ex);
            throw new AvatarUploadException();
        }
    }

    private static String getFileExtension(MultipartFile file) {
        final var originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        return "";
    }
}
