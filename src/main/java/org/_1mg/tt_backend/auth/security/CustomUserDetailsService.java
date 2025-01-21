package org._1mg.tt_backend.auth.security;


import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Member;
import org._1mg.tt_backend.auth.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org._1mg.tt_backend.base.CustomException.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String oauthId) throws UsernameNotFoundException {

        Member member = memberRepository.findByOauthIdNotDeleted(oauthId).orElseThrow(() ->
                new UsernameNotFoundException(USER_NOT_FOUND.getMessage()));

        return new CustomUserDetails(member.convertToDTO());
    }
}
