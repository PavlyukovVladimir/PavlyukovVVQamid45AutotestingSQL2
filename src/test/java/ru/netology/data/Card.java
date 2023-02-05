package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
class Card {
    Object id;
    Object user_id;
    Object number;
    Object balance_in_kopecks;

    public void setId(Object id) {
        this.id = id;
    }

    public void setUser_id(Object user_id) {
        this.user_id = user_id;
    }

    public void setNumber(Object number) {
        this.number = number;
    }

    public void setBalance_in_kopecks(Object balance_in_kopecks) {
        this.balance_in_kopecks = balance_in_kopecks;
    }
}