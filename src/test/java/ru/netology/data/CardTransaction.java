package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
class CardTransaction {
    Integer amount_in_kopecks;
    Timestamp created;
    String id;
    String source;
    String target;

    public void setAmount_in_kopecks(Integer amount_in_kopecks) {
        this.amount_in_kopecks = amount_in_kopecks;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}