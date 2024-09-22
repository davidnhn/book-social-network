import {Component, OnInit} from '@angular/core';
import {BookRequest} from "../../../../services/models/book-request";
import {BookService} from "../../../../services/services/book.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-manage-book',
  templateUrl: './manage-book.component.html',
  styleUrls: ['./manage-book.component.scss']
})
export class ManageBookComponent implements OnInit{
  errorMsg : Array<string> = [];
  // URL de la photo sélectionnée pour prévisualisation
  selectedPicture: string| undefined;
  // Fichier de la couverture du livre sélectionné
  selectedBookCover: any;
  bookRequest: BookRequest= {authorName:'', isbn:'',title:'',synopsis:''};

  constructor(
    private bookService: BookService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit() {
    const bookId = this.activatedRoute.snapshot.params['bookId'];
    if(bookId) {
      this.bookService.findBookById({
        'book-id': bookId,
      }).subscribe({
        next:(book) => {
          this.bookRequest = {
            id: book.id,
            title: book.title as string,
            synopsis: book.synopsis as string,
            isbn:book.isbn as string,
            shareable:book.shareable,
            authorName:book.authorName as string
          };
          if(book.cover) {
            this.selectedPicture = 'data:image/jpg;base64,' + book.cover
          }
        }
      })
    }
  }

  onFileSelected(event: any) {
    this.selectedBookCover = event.target.files[0];
    console.log(this.selectedBookCover);
    if(this.selectedBookCover) {
      // Création d'un FileReader pour lire le contenu du fichier
      const reader = new FileReader();
      // Définit une fonction à appeler lorsque la lecture est terminée
      reader.onload = () => {
        // Assigne le résultat de la lecture à selectedPicture pour prévisualisation
        this.selectedPicture = reader.result as string;
      }
      // Lit le fichier comme une URL de données (base64)
      reader.readAsDataURL(this.selectedBookCover); // Cette méthode démarre la lecture du fichier et une fois terminée, déclenche l'événement load.
    }
  }

  saveBook() {
    this.bookService.saveBook({
      body: this.bookRequest
    }).subscribe({
      next: (bookId) => {
        this.bookService.uploadBookCoverPicture({
          'book-id': bookId,
          body: {
            file: this.selectedBookCover
          }
        }).subscribe({
          next: () => {
            this.router.navigate(['books/my-books'])
          }
        })
      },
      error: (err) => {
        this.errorMsg = err.error.validationErrors;
      }
    })
  }
}
