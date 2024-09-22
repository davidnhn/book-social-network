package com.davidnhn.book.feedback;

import com.davidnhn.book.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {


    public Feedback toFeedback(FeedbackRequest request) {
       return   Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .shareable(false) // not required and has no impact, just to satisfy lombok
                        .archived(false)
                        .build()

                )
                .build();


    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
                .comment(feedback.getComment())
                .note(feedback.getNote())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}
