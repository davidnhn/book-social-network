import {Component, OnInit} from '@angular/core';
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {PageResponseBookResponse} from "../../../../services/models/page-response-book-response";
import {borrowBook} from "../../../../services/fn/book/borrow-book";
import {BookResponse} from "../../../../services/models/book-response";

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit{
  bookResponse: PageResponseBookResponse = {};
   page: number = 0;
   size: number = 5;
   message: string= '';
   level: string = "success";
  constructor(
    private bookService: BookService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.findAllBook()
  }

  private findAllBook() {
    this.bookService.findAllBooks({
      page: this.page,
      size: this.size
    })
      .subscribe({
      next:(books) => {
        this.bookResponse = books;
      },
      error: () => {}
    })
  }

  goToPage(page: number) {
    this.page = page;
    this.findAllBook();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBook();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBook();
  }

  goToNextPage() {
    this.page++;
    this.findAllBook();
  }

  goToLastPage() {
    this.page = this.bookResponse.totalPages as number -1  ;
    this.findAllBook()
  }

  get isLastPage(): boolean {
    return this.page == this.bookResponse.totalPages as number -1; // car page commence a 0
  }



  borrowBook(book: BookResponse) {
    this.message = ""
    this.bookService.borrowBook({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        this.level = "success";
        this.message = 'Book successfully added to your list'
      },
      error: (err)=> {
        console.log(err);
        this.level= "error";
        this.message = err.error.error;
      }
    })
  }
}
