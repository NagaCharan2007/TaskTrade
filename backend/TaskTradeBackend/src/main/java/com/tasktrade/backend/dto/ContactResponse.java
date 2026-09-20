package com.tasktrade.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactResponse {
    private String name;
    private String email;
}
