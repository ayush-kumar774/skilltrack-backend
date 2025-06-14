package org.havoc.skilltrack.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Date expiryDate;
    private String userName;
    private String role;
}
