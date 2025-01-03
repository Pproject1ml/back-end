package org._1mg.tt_backend.auth.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final MemberDTO member;

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(member.getRole().name()));

        return roles;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return getMemberId();
    }

    public String getMemberId() {
        return member.getMemberId();
    }

    public String getOauthId() {
        return member.getOauthId();
    }

    public String getEmail() {
        return member.getEmail();
    }
}
