package vn.elca.training.pilot_project_back.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Length(max = 36)
    @Pattern(regexp = "^756.\\d{4}.\\d{4}.\\d{2}$")
    private String avsNumber;
    @NotNull
    private String lastName;
    @NotNull
    private String firstName;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(precision = 17, scale = 2)
    private BigDecimal avsAmount;
    @Column(precision = 17, scale = 2)
    private BigDecimal acAmount;
    @Column(precision = 17, scale = 2)
    private BigDecimal afAmount;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;
}
