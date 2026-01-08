package com.power.trading.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String email;
    private String userType;
    private String status;
}
