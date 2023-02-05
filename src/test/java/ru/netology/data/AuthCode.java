package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
class AuthCode {
    String code;
    Timestamp created;
    String id;
    String user_id;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}