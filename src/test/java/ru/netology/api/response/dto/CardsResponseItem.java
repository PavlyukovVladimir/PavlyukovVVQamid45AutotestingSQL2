package ru.netology.api.response.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netology.data.SchemaGetter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardsResponseItem {
    public String id;
    public String number;
    public Double balance;
    @JsonIgnore
    public static String getSchema(){
        return SchemaGetter.getResponseSchema("CardsResponse");
    }
}
