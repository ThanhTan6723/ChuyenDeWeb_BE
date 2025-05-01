package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Review}
 */
@Value
public class ReviewDTO implements Serializable {
    Long id;
    String commenterName;
    String phonenumberCommenter;
    ProductDTO product;
    Integer rating;
    String comment;
    Instant dateCreated;
    Instant dateReply;
    String image;
    String response;
    Boolean isAccept;
}