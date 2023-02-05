package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
class Card {
    String id;
    String user_id;
    String number;
    Integer balance_in_kopecks;

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setBalance_in_kopecks(Integer balance_in_kopecks) {
        this.balance_in_kopecks = balance_in_kopecks;
    }
}