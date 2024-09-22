import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BookResponse} from "../../../../services/models/book-response";
import {BookListComponent} from "../../pages/book-list/book-list.component";

@Component({
  selector: 'app-book-card',
  templateUrl: './book-card.component.html',
  styleUrls: ['./book-card.component.scss']
})
export class BookCardComponent {
  private _book: BookResponse = {};
  private _bookCover: string | undefined;
  private _manage:boolean = false;

  get book(): BookResponse {
    return this._book;
  }

  @Input()
  set book(value: BookResponse) {
    this._book = value;
  }


  get bookCover(): string | undefined {
    if (this._book.cover) {
      return 'data:image/jpg;base64,' + this._book.cover
    }
    return 'https://picsum.photos/1900/800';
  }
  get manage(): boolean {
    return this._manage;
  }

  @Input()
  set manage(value: boolean) {
    this._manage = value;
  }

  @Output() private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private archive: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private addToWaitingList: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private details: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();

  onShowDetails() {
    this.details.emit(this._book);
  }

  onBorrow() {
    this.borrow.emit(this._book);

  }

  onAddToWaitingList() {
    this.addToWaitingList.emit(this._book)
  }

  onEdit() {
    this.edit.emit(this._book)
  }

  onShare() {
    this.share.emit(this._book);
  }

  onArchive() {
    this.archive.emit(this._book)
  }



}


/**
 * Composant Angular pour afficher une carte de livre.
 *
 * Méthodes déclenchées au clic :
 * Les méthodes comme `onShowDetails`, `onBorrow`, etc., sont appelées lorsqu'un utilisateur clique
 * sur les éléments correspondants dans le template HTML. Elles utilisent `EventEmitter` pour émettre
 * des événements vers le composant parent.
 *
 * Utilisation de @Output() et EventEmitter :
 * - `@Output()` marque une propriété comme un événement que le composant peut émettre.
 * - `EventEmitter` permet de créer et d'émettre des événements.
 *
 * Exemple de fonctionnement :
 * Dans le template HTML du composant enfant, `(click)="onShowDetails()"` appelle la méthode `onShowDetails()`
 * lorsqu'on clique sur l'icône. Cette méthode utilise `this.details.emit(this._book)` pour émettre un
 * événement avec les données du livre.
 *
 * Exemple d'utilisation dans le composant parent :
 * Dans le template du composant parent, `(details)="handleDetails($event)"` écoute l'événement `details` émis par
 * le composant enfant et appelle la méthode `handleDetails` avec l'événement comme argument.
 * La méthode `handleDetails` dans le parent peut alors utiliser les données du livre émises par l'enfant.
 *
 * Composant parent - Template:
 * <app-book-card (details)="handleDetails($event)" [book]="selectedBook"></app-book-card>
 *
 * Composant parent - Classe:
 * handleDetails(book: BookResponse) {
 *   console.log("Book details:", book);
 *   // Faire quelque chose avec les détails du livre, comme ouvrir une fenêtre de détails
 * }
 */
