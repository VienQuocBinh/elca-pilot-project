package vn.elca.training.pilot_project_front.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetail {
    private String object;
    private String field;
    private String value;
    private String message;
    private String fxErrorKey;
}
