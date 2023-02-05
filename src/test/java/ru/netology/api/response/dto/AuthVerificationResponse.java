package ru.netology.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netology.data.SchemaGetter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthVerificationResponse {
    String token;
    @JsonIgnore
    public static String getSchema(){
        return SchemaGetter.getResponseSchema("AuthVerificationResponse");
    }
}
