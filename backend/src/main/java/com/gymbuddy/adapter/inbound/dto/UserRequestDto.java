package com.gymbuddy.adapter.inbound.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * HTTP request payload for creating or updating a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

  private String username;
  private String email;
}
