package com.example.my_ai.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberDTO {

    @Size(max = 255)
    @MemberUsernameValid
    private String username;

    @Size(max = 255)
    private String password;

    @Size(max = 255)
    private String nickname;

    @Size(max = 255)
    @MemberEmailUnique
    private String email;

    @Size(max = 255)
    private String profileImgUrl;

}
