import {Component, OnInit} from '@angular/core';
import {BorrowedBookResponse} from "../../../../services/models/borrowed-book-response";
import {PageResponseBorrowedBookResponse} from "../../../../services/models/page-response-borrowed-book-response";
import {BookService} from "../../../../services/services/book.service";
import {BookResponse} from "../../../../services/models/book-response";
import {Router} from "@angular/router";
import {FeedbackRequest} from "../../../../services/models/feedback-request";
import {FeedbackService} from "../../../../services/services/feedback.service";

@Component({
  selector: 'app-borrowed-book-list',
  templateUrl: './borrowed-book-list.component.html',
  styleUrls: ['./borrowed-book-list.component.scss']
})
export class BorrowedBookListComponent implements OnInit{
  borrowedBooks: PageResponseBorrowedBookResponse = {};
   page: number= 0;
   size: number = 5;
   selectedBook: BorrowedBookResponse | undefined = undefined;
   feedbackRequest: FeedbackRequest = {bookId: 0, comment: "", note: 0}

  constructor(
    private bookService: BookService,
    private router: Router,
    private feedbackService: FeedbackService) {
  }

  ngOnInit(): void {
   this.findAllBorrowedBook();
  }

  returnBorrowedBook(book : BorrowedBookResponse) {
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number;
  }

  private findAllBorrowedBook() {
      this.bookService.findAllBorrowedBooks({
        page: this.page,
        size: this.size
      }).subscribe({
        next: (resp) => {
          this.borrowedBooks = resp;
        }
      });
  }

  goToPage(page: number) {
    this.page = page;
    this.findAllBorrowedBook();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBorrowedBook();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBorrowedBook();
  }

  goToNextPage() {
    this.page++;
    this.findAllBorrowedBook();
  }

  goToLastPage() {
    this.page = this.borrowedBooks.totalPages as number -1  ;
    this.findAllBorrowedBook()
  }

  get isLastPage(): boolean {
    return this.page == this.borrowedBooks.totalPages as number -1; // car page commence a 0
  }

  returnBook(withFeedback: boolean) {
     console.log("id of selected book : ", this.selectedBook?.id);
    this.bookService.returnBorrowedBook({
      "book-id": this.selectedBook?.id as number
    }).subscribe({
      next: () => {
        if(withFeedback) {
          this.giveFeedback();
        }
        this.selectedBook = undefined;
        this.findAllBorrowedBook()
      }
    })
  }

  private giveFeedback() {
    this.feedbackService.saveFeedback({
      body: this.feedbackRequest
    }).subscribe({
      next: () => {}
    })
  }
}
