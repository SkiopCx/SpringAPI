package com.codewithmosh.store.users;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    String OldPassword;
    String NewPassword;
}
