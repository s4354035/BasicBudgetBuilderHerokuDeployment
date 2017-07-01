package com.basicBudgetBuilder.domain;

import lombok.Data;
import javax.persistence.*;

/**
 * Entity class and Table for storing attachments
 * (attachments were not implemented due to time constraints)
 * Created by Hanzi Jing on 7/04/2017.
 */
@Data
@Entity
@Table(name = "attachment_body")
public class AttachmentBody {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Byte[] body;

    @OneToOne
    @JoinColumn (name = "attachment_id")
    private Attachment attachment;
}
