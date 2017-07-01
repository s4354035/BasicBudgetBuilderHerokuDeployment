package com.basicBudgetBuilder.domain;

import lombok.Data;
import javax.persistence.*;

/**
 * Entity class and Table for storing attachment file names
 * (attachments were not implemented due to time constraints)
 * Created by Hanzi Jing on 7/04/2017.
 */
@Data
@Entity
@Table(name = "attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false)
    private String fileName;

    @Column (nullable = false)
    private long size;

    @ManyToOne
    private Debit debit;
}
