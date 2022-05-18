package com.parcel.app.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseDto<T> {

    private T body;
    private HttpStatus status;

    public static <T> ResponseDto<T> created(T body){
        return new ResponseDto<>(body, HttpStatus.CREATED);
    }
}
