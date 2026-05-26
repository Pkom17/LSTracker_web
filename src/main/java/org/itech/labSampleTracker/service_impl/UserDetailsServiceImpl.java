package org.itech.labSampleTracker.service_impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.entities.AppUser;
import org.itech.labSampleTracker.service.AppUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	@Resource
	private AppUserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) {

		AppUser u = userService.findUserByLogin(username.trim());
		if (null == u) {
			throw new UsernameNotFoundException("Login " + username + " not found.");
		}

		Collection<GrantedAuthority> authorities = new ArrayList<>();
		if (ObjectUtils.isNotEmpty(u.getRole())) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + u.getRole()));
		}
		boolean accountNonExpired = ObjectUtils.isEmpty(u.getPasswordExpireAt()) ? true
				: u.getPasswordExpireAt().after(new Date());
		return new User(u.getLogin(), u.getPassword(), u.getIsActive(), accountNonExpired, true, !u.getIsLocked(),
				authorities);
	}

}
