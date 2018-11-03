package com.appsdeveloperblog.app.ws.service;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
  UserDto createUser(UserDto user);

  UserDto getUser(String email);

  UserDto getUserByUserId(String userId);

  UserDto updateUser(String id, UserDto user);

  void deleteUser(String id);

  List<UserDto> getUsers(int page, int limit);

  boolean verifyEmailToken(String token);
}
