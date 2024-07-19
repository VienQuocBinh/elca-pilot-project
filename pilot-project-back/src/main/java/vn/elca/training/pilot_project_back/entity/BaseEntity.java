package vn.elca.training.pilot_project_back.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Version
    @NotNull
    private Integer version;
    @CreatedDate
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @LastModifiedDate
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
}
