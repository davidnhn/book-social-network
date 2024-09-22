import { Component } from '@angular/core';
import {PageResponseBookResponse} from "../../../../services/models/page-response-book-response";
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {BookResponse} from "../../../../services/models/book-response";

@Component({
  selector: 'app-my-books',
  templateUrl: './my-books.component.html',
  styleUrls: ['./my-books.component.scss']
})
export class MyBooksComponent {
  bookResponse: PageResponseBookResponse = {};
  page: number = 0;
  size: number = 5;

  constructor(
    private bookService: BookService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.findAllBooksByOwner()
  }

  private findAllBooksByOwner() {
    this.bookService.findAllBooksByOwner({
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
    this.findAllBooksByOwner();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBooksByOwner();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBooksByOwner();
  }

  goToNextPage() {
    this.page++;
    this.findAllBooksByOwner();
  }

  goToLastPage() {
    this.page = this.bookResponse.totalPages as number -1  ;
    this.findAllBooksByOwner()
  }

  get isLastPage(): boolean {
    return this.page == this.bookResponse.totalPages as number -1; // car page commence a 0
  }


  archiveBook(book: BookResponse) {
    this.bookService.updateArchivedStatus({
      "book-id":book.id as number
    }).subscribe({
      next: () => {
        book.archived = !book.archived;
      }
    })
  }

  editBook(book: BookResponse) {
    this.router.navigate(["books","manage", book.id]);
  }

  shareBook(book: BookResponse) {
    this.bookService.updateShareableStatus({
      "book-id": book.id as number
    }).subscribe({
      next: () => {
        book.shareable = !book.shareable;
        // on change la valeur de shareable pour le l'ui se mette a jour sans avoir besoin de recharger et fetch a novueau les donné
      }
    })
  }
}
