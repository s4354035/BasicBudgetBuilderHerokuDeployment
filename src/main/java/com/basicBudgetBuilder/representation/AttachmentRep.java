package com.basicBudgetBuilder.representation;

import lombok.Data;

/**
 * Entity class for representing attachment files
 *(attachments were not implemented due to time constraints)
 * Created by Hanzi Jing on 8/04/2017.
 */
@Data
public class AttachmentRep {
    private long debitId;
    private String fileName;
    private long size;
    private String attachmentLink;

    public AttachmentRep(){
    }
    public AttachmentRep(long debitId,
                         String fileName,
                         long size,
                         String attachmentLink){
        this.debitId = debitId;
        this.fileName = fileName;
        this.size = size;
        this.attachmentLink = attachmentLink;
    }
}
