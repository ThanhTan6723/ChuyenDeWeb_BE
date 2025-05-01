package org.example.chuyendeweb_be.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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