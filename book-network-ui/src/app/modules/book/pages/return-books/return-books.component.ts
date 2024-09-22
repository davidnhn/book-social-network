import {Component, OnInit} from '@angular/core';
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {PageResponseBorrowedBookResponse} from "../../../../services/models/page-response-borrowed-book-response";
import {BorrowedBookResponse} from "../../../../services/models/borrowed-book-response";

@Component({
  selector: 'app-return-books',
  templateUrl: './return-books.component.html',
  styleUrls: ['./return-books.component.scss']
})
export class ReturnBooksComponent implements OnInit{
  returnedBooks: PageResponseBorrowedBookResponse = {};
  page: number= 0;
  size: number = 5;
  message: string = "";
  level: string= "success";

  constructor(
    private bookService: BookService,
    private router: Router,
   ) {
  }

  ngOnInit(): void {
    this.findAllReturnedBook();
  }



  private findAllReturnedBook() {
    this.bookService.findAllReturnedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp) => {
        this.returnedBooks = resp;
      }
    });
  }

  goToPage(page: number) {
    this.page = page;
    this.findAllReturnedBook();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllReturnedBook();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllReturnedBook();
  }

  goToNextPage() {
    this.page++;
    this.findAllReturnedBook();
  }

  goToLastPage() {
    this.page = this.returnedBooks.totalPages as number -1  ;
    this.findAllReturnedBook()
  }

  get isLastPage(): boolean {
    return this.page == this.returnedBooks.totalPages as number -1; // car page commence a 0
  }

  approveBookReturn(book: BorrowedBookResponse) {
    if(!book.returned) {
      this.level = "error";
      this.message = "The book is not yet returned";
      return;
    }

    this.bookService.approveReturnBorrowedBook({
      "book-id": book.id as number
    }).subscribe({
      next: () => {
        this.level = "success";
      this.message = "Book return approved";
      this.findAllReturnedBook();
      }
    })
  }
}
