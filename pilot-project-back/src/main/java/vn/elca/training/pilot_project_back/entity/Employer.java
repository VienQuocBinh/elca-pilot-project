package vn.elca.training.pilot_project_back.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.listener.EmployerListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(EmployerListener.class)
public class Employer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @NotNull
    private PensionType pensionType;
    @NotNull
    private String name;
    @Pattern(regexp = "^\\d{6}$")
    @Length(max = 50)
    @NotNull
    private String number;
    @NotNull
    @Length(max = 36)
    @Pattern(regexp = "^(CHE|ADM)-\\d{3}.\\d{3}.\\d{3}")
    private String ideNumber;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredDate;
}
